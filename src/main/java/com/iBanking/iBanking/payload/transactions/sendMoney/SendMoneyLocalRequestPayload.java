package com.iBanking.iBanking.payload.transactions.sendMoney;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class SendMoneyLocalRequestPayload {
    private String originatorName;
    private String amount;
    private String creditAccount;
    private String debitAccount;
    private String securityAnswer = "NA";
    private String mobileNumber;
    private String transactionType = "ACFF";
    private String pin;
    private String requestId = Generics.generateRequestId();
    private String beneficiaryName;
    private String narration;
    private String channelName = "ibank";
    private String currencyCode = "NGN";
    private String category = "TRANSFER";
    private String valueDate = "1000117425";
    private String transRef = "MOB5463123208";

}
