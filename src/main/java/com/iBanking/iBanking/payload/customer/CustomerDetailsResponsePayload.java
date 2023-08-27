package com.iBanking.iBanking.payload.customer;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDetailsResponsePayload {
    private String responseCode;
    private String responseMessage;
    private String customerNumber;
    private String firstName;
    private String lastName;
    private String middleName;
    private String maritalStatus;
    private String gender;
    private String branchCode;
    private String kycTier;
    private String status;
    private String customerType;
    private String pin;
    private String securityQuestion;
    private String securityAnswer;
    private String dailyLimit;
    private String maxBalanceLimit;
    private String perDepositLimit;
    private String perWithdrawalLimit;
    private String mobileNumber;
    private String pinTries;
    private String registered;
    private String bvn;
    private String ippisNumber;
    private String [] missingFields;
    private String remark;
}
