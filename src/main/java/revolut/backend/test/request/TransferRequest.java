package revolut.backend.test.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferRequest {

    @JsonProperty(required = true)
    private Long payerId;
    @JsonProperty(required = true)
    private Long recipientId;
    @JsonProperty(required = true)
    private Long amount;

    public TransferRequest() {
    }

    public TransferRequest(Long payerId, Long recipientId, Long amount) {
        this.payerId = payerId;
        this.recipientId = recipientId;
        this.amount = amount;
    }

    public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

}
