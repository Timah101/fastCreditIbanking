package com.iBanking.iBanking.payload.transactions.cableTv;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class ElectricityPaymentRequestPayload {
    private String requestId = Generics.generateRequestId();
    private String billerId;
    private String meterNumber;
    private String mobileNumber;
    private String debitAccount;
    private String amount;
    private String address;
    private String customerName;
    private String pin;
}
