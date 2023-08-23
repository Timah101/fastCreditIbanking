package com.iBanking.iBanking.payload.customer;

import lombok.Data;

@Data
public class UpdateCustomerRequestPayload {
    private String mobileNumber;
    private String sector;
    private String industry;
    private String bvn;
    private String idType;
    private String idNumber;
    private String idImage;
    private String maritalStatus;
    private String passportPhoto;
    private String emailAddress;
    private String requestId;
}
