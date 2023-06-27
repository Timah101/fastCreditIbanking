package com.iBanking.iBanking.payload;

import lombok.Data;

@Data
public class SendOtpResponsePayload {
    private String responseCode;
    private String responseMessage;
}
