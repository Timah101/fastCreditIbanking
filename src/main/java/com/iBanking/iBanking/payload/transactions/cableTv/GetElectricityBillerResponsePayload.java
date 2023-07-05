package com.iBanking.iBanking.payload.transactions.cableTv;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class GetElectricityBillerResponsePayload {
    private String responseCode;
    private String responseMessage;
    private List<String> errorMessage = Collections.singletonList("error fetching data");
    private List<ElectricityBillersList> biller;

}
