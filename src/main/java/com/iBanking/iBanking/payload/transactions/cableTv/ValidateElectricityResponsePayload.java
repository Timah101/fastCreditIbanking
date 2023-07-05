package com.iBanking.iBanking.payload.transactions.cableTv;

import lombok.Data;

@Data
public class ValidateElectricityResponsePayload {
    private String responseCode;
    private String responseMessage;
    private String cardHolderName;
    private String otherCustomerInfo;
}
