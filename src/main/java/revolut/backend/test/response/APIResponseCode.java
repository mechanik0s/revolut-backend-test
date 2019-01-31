package revolut.backend.test.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum APIResponseCode {

    OK(0, "Ok"),
    UNKNOWN_ERROR(1, "Unknown error"),
    NO_ROUTE(2, "No route for this path"),
    INSUFFICIENT_FUNDS(3, "Insufficient funds"),
    NO_ACCOUNT(4, "Account not found"),
    ACCOUNT_ALREADY_BLOCKED(5, "Account already blocked by transaction"),
    TRANSACTION_ERROR(6, "Transaction error");
    private static final Map<Integer, APIResponseCode> lookup = new HashMap<>();

    static {
        for (APIResponseCode responseCode : APIResponseCode.values()) {
            lookup.put(responseCode.getCode(), responseCode);
        }
    }

    private final Integer code;
    private final String message;

    APIResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static APIResponseCode get(Integer code) {
        return lookup.get(code);
    }

    @JsonProperty
    public Integer getCode() {
        return code;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }
}

