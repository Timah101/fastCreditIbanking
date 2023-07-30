package com.iBanking.iBanking.payload.customer;

import lombok.Data;

@Data
public class ResetPinRequestPayload {

    private String mobileNumber;
    private String securityQuestion;
    private String securityAnswer;
    private String pin;
    private String password;
    private String requestId;
    private String otp;
}
