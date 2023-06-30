package com.iBanking.iBanking.payload.transactions.cableTv;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class CableTvPaymentRequestPayload {

    private String requestId = Generics.generateRequestId();
    private String billerId;
    private String smartCard;
    private String mobileNumber;
    private String debitAccount;
    private String amount;
    private String productId;
    private String invoicePeriod;
    private String customerName;
    private String pin;
    private String token = "tyrueieoe";
}
