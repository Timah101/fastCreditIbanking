package com.iBanking.iBanking.payload.transactions.airtimeData;


import lombok.Data;

@Data
public class DataPlansList {
    private String id;
    private String telco;
    private String planDescription;
    private String amount;
    private String dataPeriod;
    private String status;
    private String itemName;
    private String vendor;
    }
