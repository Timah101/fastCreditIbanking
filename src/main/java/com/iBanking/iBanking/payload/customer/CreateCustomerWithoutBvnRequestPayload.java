package com.iBanking.iBanking.payload.customer;

import lombok.Data;

@Data
public class CreateCustomerWithoutBvnRequestPayload {

    private String mobileNumber;
    private String firstName;
    private String lastName;
    private String gender;
    private String otherName;
    private String residenceAddress;
    private String accountOfficerCode;
    private String sector;
    private String industry;
    private String dob;
    private String ippisNumber;
    private String retirementAge;
    private String bvn;
    private String officePhoneNumber;
    private String idType;
    private String idNumber;
    private String branchCode;
    private String maritalStatus;
    private String passportPhoto;
    private String signatureImage;
    private String requestId;
    private String utilityBill;
    private String utilityBillImage;
    private String title;
    private String nationality;
    private String residenceState;
    private String residenceCity;
    private String emailAddress;
    private String spouseName;
    private String employmentStatus;
    private String employerName;
    private String employerAddress;
    private String employerCity;
    private String occupation;
    private String employmentDate;
    private String employmentNumber;
    private String loanType = "First Loan";
    private String kycTier = "3";
    private String referredBy;
    private String otp;
    private String deviceId = "dv12345678";

}
