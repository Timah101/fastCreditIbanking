package com.iBanking.iBanking.payload.transactions.cableTv;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class GetCableTvBillersRequestPayload {
    private String requestId = Generics.generateRequestId();
    private String biller;
}
