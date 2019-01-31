package revolut.backend.test.banking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revolut.backend.test.exceptions.AccountNotFoundException;

public interface AccountService {
    Logger logger = LoggerFactory.getLogger("Account-service");

    Account findAccount(Long id) throws AccountNotFoundException;

    void addAccount(Account account);
}
