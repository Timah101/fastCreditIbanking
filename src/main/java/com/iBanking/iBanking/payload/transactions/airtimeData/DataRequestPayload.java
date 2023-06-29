package com.iBanking.iBanking.payload.transactions.airtimeData;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class DataRequestPayload {
    private String mobileNumber;
    private String debitAccount;
    private String telco;
    private String amount;
    private String pin;
    private String requestId = Generics.generateRequestId();
    private String token;
    private String dataPlanId;
}
