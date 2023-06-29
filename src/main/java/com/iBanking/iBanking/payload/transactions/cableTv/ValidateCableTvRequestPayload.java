package com.iBanking.iBanking.payload.transactions.cableTv;

import lombok.Data;

@Data
public class ValidateCableTvRequestPayload {
    private String biller;
    private String requestId;
    private String cardNumber;
}
