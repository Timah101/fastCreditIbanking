package com.iBanking.iBanking.payload.transactions.sendMoney;

import lombok.Data;

@Data
public class GetBankList {
    private String id;
    private String bank;
    private String bankCategory;
    private String bankCode;
}
