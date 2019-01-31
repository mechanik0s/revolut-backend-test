package revolut.backend.test.banking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface AccountService {
    Logger logger = LoggerFactory.getLogger("Account-service");

    Account findAccount(Long id);

    void addAccount(Account account);
}
