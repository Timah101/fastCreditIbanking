package com.iBanking.iBanking.payload;

import lombok.Data;

@Data
public class SendOtpPayloadRequest {
    private String mobileNumber;
    private String purpose;
}
