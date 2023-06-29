package com.iBanking.iBanking.payload.transactions.cableTv;

import com.iBanking.iBanking.payload.transactions.cableTv.CableTvBillersList;
import lombok.Data;

import java.util.List;

@Data
public class GetCableTvBillersResponsePayload {
    private String responseCode;
    private String responseMessage;
    private List<CableTvBillersList> billers;
}
