package com.iBanking.iBanking.payload.customer;

import lombok.Data;

@Data
public class MissingFieldsPayload {

    private String deviceId;
    private String industry;
    private String bvn;
    private String idType;
    private String idNumber;
    private String passportPhoto;
    private String securityQuestion;
    private String securityAnswer;
    private String pin;
    private String password;
}
