package com.iBanking.iBanking.payload.customer;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class LoginRequestPayload {
    private String mobileNumber;
    private String authParam = "Password";
    private String authValue;
    private String requestId = Generics.generateRequestId();
    private String deviceId;
}
