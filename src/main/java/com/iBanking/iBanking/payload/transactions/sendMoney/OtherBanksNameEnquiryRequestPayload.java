package com.iBanking.iBanking.payload.transactions.sendMoney;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class OtherBanksNameEnquiryRequestPayload {
//    private String responseCode;
//    private String responseMessage;
    private String destinationInstitutionCode;
    private String accountNumber;
    private String ChannelCode = "8";
    private String InstitutionCode = "050009";
    private String ChannelName = "InternetBanking";
}
