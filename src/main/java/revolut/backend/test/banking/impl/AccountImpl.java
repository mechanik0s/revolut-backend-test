package revolut.backend.test.banking.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import revolut.backend.test.banking.Account;
import revolut.backend.test.exceptions.InsufficientFundsException;
import revolut.backend.test.exceptions.TransactionErrorException;

public class AccountImpl implements Account {
    private final long accountId;
    private final String accountName;
    private long balance;
    private long uncommittedBalance = 0L;
    private TransactionIdentifier currentTransaction = null;


    @JsonCreator
    public AccountImpl(@JsonProperty("accountId") Long accountId, @JsonProperty("accountName") String accountName, @JsonProperty("balance") Long balance) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "AccountImpl{" +
                "accountId=" + accountId +
                ", accountName='" + accountName + '\'' +
                ", balance=" + balance +
                '}';
    }

    @Override
    public long accountId() {
        return accountId;
    }

    @Override
    public String accountName() {
        return accountName;
    }

    @Override
    public synchronized long balance() {
        return balance;
    }

    @Override
    public synchronized void deposit(TransactionIdentifier t, long amount)
            throws InsufficientFundsException, TransactionErrorException {
        withdraw(t, -amount);
    }

    @Override
    public synchronized void withdraw(TransactionIdentifier t, long amount)
            throws InsufficientFundsException, TransactionErrorException {
        if (t != currentTransaction) throw new TransactionErrorException();
        if (uncommittedBalance < amount)
            throw new InsufficientFundsException();
        uncommittedBalance -= amount;
    }

    @Override

    public synchronized boolean join(TransactionIdentifier t) {
        if (currentTransaction != null) return false;
        currentTransaction = t;
        uncommittedBalance = balance;
        return true;
    }

    @Override
    public synchronized boolean canCommit(TransactionIdentifier t) {
        return (t == currentTransaction);
    }

    @Override
    public synchronized void abort(TransactionIdentifier t) {
        if (t == currentTransaction)
            currentTransaction = null;
    }

    @Override
    public synchronized void commit(TransactionIdentifier t) throws TransactionErrorException {
        if (t != currentTransaction) throw new TransactionErrorException();
        balance = uncommittedBalance;
        currentTransaction = null;
    }
}
