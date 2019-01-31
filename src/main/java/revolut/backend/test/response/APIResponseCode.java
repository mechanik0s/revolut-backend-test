package revolut.backend.test.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum APIResponseCode {

    OK(0, "Ok"),
    UNKNOWN_ERROR(1, "Unknown error"),
    NO_ROUTE(2, "No route for this path"),
    INSUFFICIENT_FUNDS(3, "Insufficient funds"),
    NO_ACCOUNT(4, "Account not found"),
    ACCOUNT_ALREADY_BLOCKED(5, "Account already blocked by transaction"),
    TRANSACTION_ERROR(6, "Transaction error");
    private final Integer code;
    private final String message;


    APIResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @JsonProperty
    Integer getCode() {
        return code;
    }

    @JsonProperty
    String getMessage() {
        return message;
    }
}

