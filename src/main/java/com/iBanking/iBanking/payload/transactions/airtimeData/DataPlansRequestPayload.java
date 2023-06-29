package com.iBanking.iBanking.payload.transactions.airtimeData;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class DataPlansRequestPayload {
    private String telco;
    private String requestId = Generics.generateRequestId();
}
