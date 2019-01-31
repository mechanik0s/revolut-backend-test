package revolut.backend.test.banking.impl;

import revolut.backend.test.banking.Account;
import revolut.backend.test.banking.AccountService;
import revolut.backend.test.exceptions.AccountNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Absolutely primitive thread-safe implementation
 * Yes, we don't generate id and may accidentally replace existing account in storage, but this is not critical for the purpose of the task.
 */
public class AccountServiceImpl implements AccountService {
    private final Map<Long, Account> accountStorage = new ConcurrentHashMap<>();

    @Override
    public Account findAccount(Long id) throws AccountNotFoundException {
        Account account = accountStorage.get(id);
        if (account == null) throw new AccountNotFoundException();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        accountStorage.put(account.accountId(), account);
        logger.debug("Account saved in storage {}", account);
    }
}
