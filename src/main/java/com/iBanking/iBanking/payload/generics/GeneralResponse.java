package com.iBanking.iBanking.payload.generics;

import lombok.Data;

@Data
public class GeneralResponse {
    private String responseCode;
    private String responseMessage;
    private String transactionRef;
    private String userAcct;
}
