package com.iBanking.iBanking.payload.transactions.cableTv;


import lombok.Data;

@Data
public class ValidateCableTvResponsePayload {
    private String responseCode;
    private String cardholderName;
    private String otherCustomerInfo;
    private String responseMessage;
}
