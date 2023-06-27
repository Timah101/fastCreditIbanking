package com.iBanking.iBanking.payload.transactions;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class SendMoneyLocalRequestPayload {

    private String originatorName;
    private String amount;
    private String creditAccount;
    private String debitAccount;
    private String securityAnswer;
    private String mobileNumber;
    //    private String deviceId;
    private String transactionType = "AC";
    private String pin;
    private String requestId = Generics.generateRequestId();
    private String beneficiaryName;
    private String narration;
    private String channelName = "ibank";
    private String currencyCode = "NGN";

}
