package com.iBanking.iBanking.payload.transactions.sendMoney;

import lombok.Data;

@Data
public class OtherBanksNameEnquiryResponsePayload {
    private String responseCode;
    private String responseDescription;

    private String DestinationInstitutionCode;
    private String AccountNumber;
    private String AccountName;
    private String BankVerificationNo;
    private String KycLevel;
    private String ResponseCode;
    private String ResponseDescription;
    private String NameEnquiryRef;
    private String ChannelCode;
}
