package com.iBanking.iBanking.payload.transactions.airtimeData;

import lombok.Data;

import java.util.List;

@Data
public class DataPlansResponsePayload {
    private String responseCode;
    private List<DataPlansList> dataPlans;
}
