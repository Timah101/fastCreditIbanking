package com.iBanking.iBanking.payload.transactions.cableTv;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class GetElectricityBillerRequestPayload {
    private String biller;
    private String requestId = Generics.generateRequestId();
}
