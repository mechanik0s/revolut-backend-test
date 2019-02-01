package revolut.backend.test.banking.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revolut.backend.test.banking.Account;
import revolut.backend.test.banking.TransferService;
import revolut.backend.test.exceptions.AccountAlreadyBlocked;
import revolut.backend.test.exceptions.InsufficientFundsException;
import revolut.backend.test.exceptions.TransactionErrorException;
import revolut.backend.test.exceptions.TransferException;

public class TransferServiceImpl implements TransferService {
    private final Logger logger;


    public TransferServiceImpl() {
        this.logger = LoggerFactory.getLogger("Transfer-service");
    }


    private void rollback(TransactionIdentifier t, long amount,
                          Account from, Account to) {
        from.abort(t);
        to.abort(t);
        logger.debug("Rollback transfer {} standard units\n from: {}\n to: {}\n {}", amount, from, to, t);
    }

    @Override
    public void transfer(Account from, Account to, long amount)
            throws TransferException, TransactionErrorException {

        if (from == null || to == null)
            throw new IllegalArgumentException();
        if (from == to) return;

        TransactionIdentifier t = new TransactionIdentifier();
        logger.debug("Start transfer {} standard units\n from: {}\n to: {}\n {}", amount, from, to, t);

        if (!from.join(t) || !to.join(t)) {
            rollback(t, amount, from, to);
            throw new AccountAlreadyBlocked();
        }

        try {
            from.withdraw(t, amount);
            to.deposit(t, amount);
        } catch (InsufficientFundsException | TransactionErrorException e) {
            rollback(t, amount, from, to);
            throw e;
        }

        if (!from.canCommit(t) || !to.canCommit(t)) {
            rollback(t, amount, from, to);
            throw new TransactionErrorException();
        }

        try {
            from.commit(t);
            to.commit(t);
            logger.debug("YAY! Successfully transfer {} standard units\n from: {}\n to: {}\n {}", amount, from, to, t);
        } catch (TransactionErrorException e) {
            rollback(t, amount, from, to);
            throw e;
        }

    }

}
