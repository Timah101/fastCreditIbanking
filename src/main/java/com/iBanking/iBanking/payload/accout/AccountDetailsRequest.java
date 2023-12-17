package com.iBanking.iBanking.payload.accout;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class AccountDetailsRequest {
    private String mobileNumber;
    private String accountNumber;
    private String requestId = Generics.generateRequestId();
    private String deviceId;
}
