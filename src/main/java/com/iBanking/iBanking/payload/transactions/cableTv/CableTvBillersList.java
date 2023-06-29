package com.iBanking.iBanking.payload.transactions.cableTv;

import lombok.Data;

@Data
public class CableTvBillersList {
    private String id;
    private String biller;
    private String bouquet;
    private String packageName;
    private String amount;
    private String monthlyAmount;
    private String availableMonths;
    private String yearlyAmount;
    private String billerId;
    private String productId;
    private String status;
    private String vendor;
}
