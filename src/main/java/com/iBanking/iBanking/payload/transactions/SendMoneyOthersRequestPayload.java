package com.iBanking.iBanking.payload.transactions;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class SendMoneyOthersRequestPayload {

    private String destinationBankCode;
    private String beneficiaryAccountNumber;
    private String currencyCode = "NGN";
    private String amount;
    private String beneficiaryAccountName;
    private String beneficiaryBvn;
    private String beneficiaryKycLevel;
    private String channelName = "ibank";
    private String debitTheirRef;
    private String paymentDetails;
    private String debitAccountNumber;
    private String mobileNumber;
    private String pin;
    private String requestId = Generics.generateRequestId();

}
