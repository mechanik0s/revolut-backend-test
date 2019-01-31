package revolut.backend.test.banking;

import revolut.backend.test.exceptions.AccountAlreadyBlocked;
import revolut.backend.test.exceptions.InsufficientFundsException;
import revolut.backend.test.exceptions.TransactionErrorException;

public interface TransferService {

    void transfer(Account from, Account to, long amount)
            throws AccountAlreadyBlocked, InsufficientFundsException, TransactionErrorException;
}
