package com.iBanking.iBanking.payload.customer;

import lombok.Data;

@Data
public class CreateCustomerWithoutBvnRequestPayload {
    private String mobileNumber;
    private String sector;
    private String industry;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String gender;
    private String idType;
    private String idNumber;
    private String maritalStatus;
    private String passportPhoto;
    private String emailAddress;
    private String requestId;
    private String deviceId = "dv1234567";
}
