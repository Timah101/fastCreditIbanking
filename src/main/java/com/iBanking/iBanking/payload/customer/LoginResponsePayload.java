package com.iBanking.iBanking.payload.customer;

import lombok.Data;

@Data
public class LoginResponsePayload {
    private String responseCode;
    private String responseMessage;
}
