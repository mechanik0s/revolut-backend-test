package revolut.backend.test.banking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import revolut.backend.test.banking.impl.AccountImpl;
import revolut.backend.test.banking.impl.TransactionIdentifier;
import revolut.backend.test.exceptions.InsufficientFundsException;
import revolut.backend.test.exceptions.TransactionErrorException;

@JsonDeserialize(as = AccountImpl.class)
public interface Account extends Transactional {
    @JsonProperty
    Long accountId();

    //just for convenience sake
    @JsonProperty
    String accountName();

    // of course, we should use Big Decimal or special money-class
    @JsonProperty
    long balance();

    void deposit(TransactionIdentifier t, long amount)
            throws InsufficientFundsException, TransactionErrorException;

    void withdraw(TransactionIdentifier t, long amount)
            throws InsufficientFundsException, TransactionErrorException;

}
