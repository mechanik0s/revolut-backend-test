package revolut.backend.test.banking;

import org.testng.annotations.Test;
import revolut.backend.test.banking.impl.AccountImpl;
import revolut.backend.test.banking.impl.TransferServiceImpl;
import revolut.backend.test.exceptions.AccountAlreadyBlocked;
import revolut.backend.test.exceptions.InsufficientFundsException;
import revolut.backend.test.exceptions.TransactionErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

/**
 * Dirty and primitive tests for check synchronization correctness
 * It is expected that total bank amount will remain unchanged after any number of transactions
 */
public class TransferServiceTest {

    private static final long MAX_AMOUNT = 100;
    private static final long MAX_ACCOUNT = 100;
    private static final int TRANSACTIONS = 10000;
    private static final int N_THREADS = 10;

    static long getTotalBalance(List<Account> accounts) {
        int total = 0;
        for (Account account : accounts) {
            total += account.balance();
        }
        return total;
    }

    @Test(expectedExceptions = InsufficientFundsException.class)
    void naiveTest() throws AccountAlreadyBlocked, InsufficientFundsException, TransactionErrorException {
        TransferServiceImpl bank = new TransferServiceImpl();
        Account crc = new AccountImpl(1L, "A", 100);
        Account dst = new AccountImpl(2L, "B", 50);
        bank.transfer(crc, dst, 10);
        assertEquals(crc.balance(), 90L);
        assertEquals(dst.balance(), 60);
        bank.transfer(crc, dst, 100); // rollback and InsufficientFundsException expected
    }

    @Test
    void stressTestSingleThread() {
        TransferServiceImpl bank = new TransferServiceImpl();
        ArrayList<Account> accounts = new ArrayList<>();
        for (long i = 0; i < MAX_ACCOUNT; i++) {
            accounts.add(new AccountImpl(i, "TEST", 100));
        }
        long startBalance = getTotalBalance(accounts);
        for (int i = 0; i < TRANSACTIONS; i++) {
            int toAccount = (int) (Math.random() * MAX_ACCOUNT);
            int fromAccount = (int) (Math.random() * MAX_ACCOUNT);
            if (toAccount == fromAccount) continue;
            int amount = (int) (Math.random() * MAX_AMOUNT);
            if (amount == 0) continue;
            try {
                bank.transfer(accounts.get(fromAccount), accounts.get(toAccount), amount);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        assertEquals(getTotalBalance(accounts), startBalance);
    }

    @Test
    void stressTestMultiThread() throws InterruptedException {
        TransferServiceImpl bank = new TransferServiceImpl();
        ArrayList<Account> accounts = new ArrayList<>();
        for (long i = 0; i < MAX_ACCOUNT; i++) {
            accounts.add(new AccountImpl(i, "TEST", 100));
        }
        long startBalance = getTotalBalance(accounts);
        ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);
        for (int i = 0; i < TRANSACTIONS; i++) {
            int from = (int) (Math.random() * MAX_ACCOUNT);
            int to = (int) (Math.random() * MAX_ACCOUNT);
            if (to == from) continue;
            int amount = (int) (Math.random() * MAX_AMOUNT);
            if (amount == 0) continue;
            executor.submit(() -> {
                try {
                    bank.transfer(accounts.get(from), accounts.get(to), amount);
                } catch (AccountAlreadyBlocked | InsufficientFundsException | TransactionErrorException e) {
                    System.out.println(e);
                }
            });
        }
        // wait until all transfer tasks finished
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        assertEquals(getTotalBalance(accounts), startBalance);

    }
}
