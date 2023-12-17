package com.iBanking.iBanking.payload.customer;

import lombok.Data;

@Data
public class CustomerDetailsResponse {
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
    private String[] missingFields;
    private String remark;


    public String getFirstName() {
        if (firstName == null || firstName.isEmpty()) {
            return "";
        }
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        if (lastName == null || lastName.isEmpty()) {
            return "";
        }
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        if (middleName == null || middleName.isEmpty()) {
            return "";
        }
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
}
