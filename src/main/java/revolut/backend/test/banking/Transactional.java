package revolut.backend.test.banking;

import revolut.backend.test.banking.impl.TransactionIdentifier;
import revolut.backend.test.exceptions.TransactionErrorException;

public interface Transactional {
    boolean join(TransactionIdentifier t);

    boolean canCommit(TransactionIdentifier t);

    void commit(TransactionIdentifier t) throws TransactionErrorException;

    void abort(TransactionIdentifier t);

}
