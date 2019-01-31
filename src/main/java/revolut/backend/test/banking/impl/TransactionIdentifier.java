package revolut.backend.test.banking.impl;

import java.util.UUID;

public class TransactionIdentifier {
    // maybe should use atomic counter variable
    private final UUID uuid = UUID.randomUUID();

    @Override
    public String toString() {
        return "TransactionIdentifier{" +
                "uuid=" + uuid +
                '}';
    }

    public UUID getUuid() {
        return uuid;
    }

}
