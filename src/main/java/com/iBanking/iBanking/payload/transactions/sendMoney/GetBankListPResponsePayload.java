package com.iBanking.iBanking.payload.transactions.sendMoney;

import lombok.Data;

import java.util.List;

@Data
public class GetBankListPResponsePayload {
    private String responseCode;
    private String responseMessage;
    private List<GetBankList> bankList;
}
