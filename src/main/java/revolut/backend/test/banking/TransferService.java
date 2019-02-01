package revolut.backend.test.banking;

import revolut.backend.test.exceptions.TransactionErrorException;
import revolut.backend.test.exceptions.TransferException;

public interface TransferService {

    void transfer(Account from, Account to, long amount)
            throws TransferException, TransactionErrorException;
}
