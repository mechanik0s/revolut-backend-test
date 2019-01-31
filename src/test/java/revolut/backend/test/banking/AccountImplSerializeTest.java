package revolut.backend.test.banking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;
import revolut.backend.test.banking.impl.AccountImpl;

import static org.testng.Assert.assertEquals;

public class AccountImplSerializeTest {

    @Test
    void serializeTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = new AccountImpl(101L, "TEST ACC", 10);
        assertEquals(mapper.writeValueAsString(account), "{\"accountId\":101,\"accountName\":\"TEST ACC\",\"balance\":10}");
    }
}