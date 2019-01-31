import io.netty.handler.codec.http.HttpResponseStatus;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import revolut.backend.test.Main;
import revolut.backend.test.banking.Account;
import revolut.backend.test.banking.impl.AccountImpl;
import revolut.backend.test.request.TransferRequest;
import revolut.backend.test.response.APIResponseCode;
import revolut.backend.test.response.BaseResponse;

import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;

public class APITest {

    public static final long FIRST_TRANSACTION_AMOUNT = 10L;
    private static final Long FIRST_ACC_ID = 1L;
    private static final Long SECOND_ACC_ID = 2L;
    private static final Long FIRST_ACC_INIT_BALANCE = 100L;
    private static final Long SECOND_ACC_INIT_BALANCE = 50L;
    private static final String CODE = "code";

    @BeforeClass
    public void setup() {
        RequestSpecification requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8080)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .addFilter(new ResponseLoggingFilter())
                .build();
        ResponseSpecification responseSpec = new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();

        RestAssured.requestSpecification = requestSpec;
        RestAssured.responseSpecification = responseSpec;
        Main.main(new String[0]);
    }

    @Test
    public void whenInvalidRoute() {
        when().get(EndPoints.invalid).then()
                .statusCode(HttpResponseStatus.NOT_FOUND.code()).assertThat()
                .body(CODE, equalTo(APIResponseCode.NO_ROUTE.getCode()));
        when().put(EndPoints.accounts).then()
                .statusCode(HttpResponseStatus.NOT_FOUND.code()).assertThat()
                .body(CODE, equalTo(APIResponseCode.NO_ROUTE.getCode()));
    }

    @Test
    public void whenRequestWithoutOrInvalidParams() {
        when().get(EndPoints.accounts).then()
                .statusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).assertThat()
                .body(CODE, equalTo(APIResponseCode.UNKNOWN_ERROR.getCode()));
        with().param("accountId", "invalid").get(EndPoints.accounts).then()
                .statusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).assertThat()
                .body(CODE, equalTo(APIResponseCode.UNKNOWN_ERROR.getCode()));

    }

    @Test
    public void whenRequestWithInvalidBody() {
        with()
                .body("{\n" +
                        "\t\"accountName\":\"invalid\",\n" +
                        "\t\"balance\": 100\n" +
                        "}")
                .post(EndPoints.accounts).then()
                .statusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).assertThat()
                .body(CODE, equalTo(APIResponseCode.UNKNOWN_ERROR.getCode()));
        when().post(EndPoints.accounts).then()
                .statusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).assertThat()
                .body(CODE, equalTo(APIResponseCode.UNKNOWN_ERROR.getCode()));
    }

    @Test
    public void whenAccountNotFound() {
        with().param("accountId", "1")
                .get(EndPoints.accounts).then()
                .statusCode(HttpResponseStatus.NOT_FOUND.code()).assertThat()
                .body(CODE, equalTo(APIResponseCode.NO_ACCOUNT.getCode()));
    }

    @Test(dependsOnMethods = "whenAccountNotFound")
    public void whenAccountCreateSuccessful() {
        with()
                .body(new AccountImpl(FIRST_ACC_ID, "Test", FIRST_ACC_INIT_BALANCE))
                .post(EndPoints.accounts).then()
                .statusCode(HttpResponseStatus.OK.code()).assertThat()
                .body(CODE, equalTo(APIResponseCode.OK.getCode()));
        with()
                .body(new AccountImpl(SECOND_ACC_ID, "Test2", SECOND_ACC_INIT_BALANCE))
                .post(EndPoints.accounts).then()
                .statusCode(HttpResponseStatus.OK.code()).assertThat()
                .body(CODE, equalTo(APIResponseCode.OK.getCode()));
    }

    @Test(dependsOnMethods = "whenAccountCreateSuccessful")
    public void whenAccountFoundSuccessful() {
        assertEquals(getAccBalance(FIRST_ACC_ID), FIRST_ACC_INIT_BALANCE);
        assertEquals(getAccBalance(SECOND_ACC_ID), SECOND_ACC_INIT_BALANCE);

    }

    private Long getAccBalance(Long accountId) {
        return with().param("accountId", accountId)
                .get(EndPoints.accounts).then()
                .statusCode(HttpResponseStatus.OK.code())
                .body(CODE, equalTo(APIResponseCode.OK.getCode()))
                .extract().body().as(new TypeRef<BaseResponse<Account>>() {
                })
                .getPayload().balance();
    }

    @Test(dependsOnMethods = "whenAccountFoundSuccessful")
    public void whenTransferSuccessful() {
        TransferRequest transferRequest = new TransferRequest(FIRST_ACC_ID, SECOND_ACC_ID, FIRST_TRANSACTION_AMOUNT);
        with().body(transferRequest)
                .post(EndPoints.transfer).then()
                .statusCode(HttpResponseStatus.OK.code())
                .body(CODE, equalTo(APIResponseCode.OK.getCode()));
        Long firstExpBalance = FIRST_ACC_INIT_BALANCE - FIRST_TRANSACTION_AMOUNT;
        Long secondExpBalance = SECOND_ACC_INIT_BALANCE + FIRST_TRANSACTION_AMOUNT;
        assertEquals(getAccBalance(FIRST_ACC_ID), firstExpBalance);
        assertEquals(getAccBalance(SECOND_ACC_ID), secondExpBalance);
    }

    @Test(dependsOnMethods = "whenTransferSuccessful")
    public void whenInsufficientFunds() {
        Long amount = 1000L;
        TransferRequest transferRequest = new TransferRequest(FIRST_ACC_ID, SECOND_ACC_ID, amount);
        with().body(transferRequest)
                .post(EndPoints.transfer).then()
                .statusCode(HttpResponseStatus.BAD_REQUEST.code())
                .body(CODE, equalTo(APIResponseCode.INSUFFICIENT_FUNDS.getCode()));
        Long firstExpBalance = FIRST_ACC_INIT_BALANCE - FIRST_TRANSACTION_AMOUNT;
        Long secondExpBalance = SECOND_ACC_INIT_BALANCE + FIRST_TRANSACTION_AMOUNT;
        assertEquals(getAccBalance(FIRST_ACC_ID), firstExpBalance);
        assertEquals(getAccBalance(SECOND_ACC_ID), secondExpBalance);
    }


    public final class EndPoints {
        static final String accounts = "/account";
        static final String transfer = "/transfer";
        static final String invalid = "/invalid";
    }

}
