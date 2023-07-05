package com.iBanking.iBanking.payload.transactions.cableTv;

import lombok.Data;

@Data
public class ElectricityBillersList {
    private String id;
    private String biller;
    private String bouquet;
    private String packageName;
    private String billerId;
    private String status;
    private String vendor;
}
