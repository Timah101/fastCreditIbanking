package com.iBanking.iBanking.payload.generics;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class MobileNumberRequestPayload {
    private String mobileNumber;
    private String requestId = Generics.generateRequestId();
}
