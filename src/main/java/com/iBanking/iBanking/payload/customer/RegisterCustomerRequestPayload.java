package com.iBanking.iBanking.payload.customer;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class RegisterCustomerRequestPayload {

    private String mobileNumber;
    private String securityQuestion;
    private String securityAnswer;
    private String pin;
    private String password;
    private String requestId = Generics.generateRequestId();
    private String otp;
    private String referredBy;
    private String referralCode;
}
