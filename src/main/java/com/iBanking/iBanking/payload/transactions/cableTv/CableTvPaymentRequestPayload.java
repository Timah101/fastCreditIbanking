package com.iBanking.iBanking.payload.transactions.cableTv;

import lombok.Data;

@Data
public class CableTvPaymentRequestPayload {

    private String requestId;
    private String billerId;
    private String meterNumber;
    private String mobileNumber;
    private String debitAccount;
    private String amount;
    private String address;
    private String customerName;
    private String token;
}
