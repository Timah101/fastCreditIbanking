package com.iBanking.iBanking.payload.customer;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class CustomerDetailsRequestPayload {
    private String mobileNumber;
    private String requestId = Generics.generateRequestId();
}
