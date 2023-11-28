package com.iBanking.iBanking.payload.accout;

import lombok.Data;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;

@Data
public class AccountList {
    private String createdAt;
    private String accountNumber;
    private String category;
    private String branchCode;
    private String customerNumber;
    private String status;

    public String getAvailableBalance() {
        Currency nairaCurrency = Currency.getInstance("NGN");
        NumberFormat currencyFormatNaira = NumberFormat.getCurrencyInstance();
        currencyFormatNaira.setCurrency(nairaCurrency);
        double numericBalance = Double.parseDouble(availableBalance);
        String formattedNaira = currencyFormatNaira.format(numericBalance);
        return formattedNaira.replace("NGN", "N");
    }


    private String availableBalance;
    private String ledgerBalance;
    private String accountName;
    private String responseCode;
    private String currencyCode;
}
