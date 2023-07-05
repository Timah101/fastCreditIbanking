package com.iBanking.iBanking.payload.generics;

import lombok.Data;

@Data
public class GeneralResponsePayload {
    private String responseCode;
    private String responseMessage;
}
