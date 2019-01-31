package revolut.backend.test.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    @JsonUnwrapped
    @JsonProperty(access = READ_ONLY)
    private APIResponseCode apiResponseCode = APIResponseCode.OK;

    @JsonProperty("payload")
    private T payload;

    private BaseResponse() {
    }

    private BaseResponse(APIResponseCode apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }

    private BaseResponse(T payload) {
        this.payload = payload;
    }

    private BaseResponse(APIResponseCode apiResponseCode, T payload) {
        this.payload = payload;
        this.apiResponseCode = apiResponseCode;
    }

    public static BaseResponse<Void> create() {
        return new BaseResponse<>();
    }

    public static BaseResponse<Void> create(APIResponseCode status) {
        return new BaseResponse<>(status);
    }

    public static <T> BaseResponse<T> create(T payload) {
        return new BaseResponse<>(payload);
    }

    public static <T> BaseResponse<T> create(APIResponseCode status, T payload) {
        return new BaseResponse<>(status, payload);
    }

    public APIResponseCode getApiResponseCode() {
        return apiResponseCode;
    }

    public T getPayload() {
        return payload;
    }
}
