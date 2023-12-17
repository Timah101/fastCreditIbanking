package com.iBanking.iBanking.payload.customer;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String mobileNumber;
    private String securityQuestion;
    private String securityAnswer;
    private String updateParam = "PASSWORD";
    private String newPassword;
    private String requestId = Generics.generateRequestId();
    private String otp;
}
