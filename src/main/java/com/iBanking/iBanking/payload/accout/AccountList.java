package com.iBanking.iBanking.payload.accout;

import lombok.Data;

@Data
public class AccountList {
    private String createdAt;
    private String accountNumber;
    private String category;
    private String branchCode;
    private String customerNumber;
    private String status;
    private String availableBalance;
    private String ledgerBalance;
    private String accountName;
    private String responseCode;
    private String currencyCode;
}
