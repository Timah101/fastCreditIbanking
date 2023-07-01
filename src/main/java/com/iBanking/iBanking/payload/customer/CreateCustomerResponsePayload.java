package com.iBanking.iBanking.payload.customer;

import lombok.Data;

@Data
public class CreateCustomerResponsePayload {
    private String responseCode;
    private String responseMessage;
}
