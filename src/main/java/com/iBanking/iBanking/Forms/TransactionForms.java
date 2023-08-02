package com.iBanking.iBanking.Forms;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class TransactionForms {
    private String mobileNumber;
    @NotEmpty(message = "{mobile-number.notEmpty}")
    @Size(min = 11, max = 13, message = "{mobile-number.size}")
    private String beneficiaryMobileNumber;
    @Email
    @NotNull(message = "{field.not-null}")
    @NotEmpty(message = "{field.not-empty}")
    private String email;
//    @Size(min = 8, message = "{password.size}")
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String password;
//    @Size(min = 5, message = "{security-question.size}")
    private String securityQuestion;
//    @Size(min = 5, message = "{security-answer.size}")
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String securityAnswer;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String pin;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String dataPin;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String otp;
    private String referredBy;
    private String referralCode;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String amount;

    private String narration;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String telco;
    private String date = String.valueOf(LocalDate.now());
    private String debitAccount;
    private String creditAccount;
    private String dataPlans;
    private String beneficiaryBank;
    private String beneficiaryName;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String smartCardNumber;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String meterNumber;
    private String biller;
    private String electricityBillerSelect;

//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String lastName;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String residenceAddress;
    private String accountOfficerCode;
    private String sector;
    private String industry;
    private String bvn;
    private String officePhoneNumber;
    private String idType;
    private String idNumber;
    private String branchCode;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String maritalStatus;
    private String passportPhoto;
    private String signatureImage;
    private String requestId;
    private String utilityBill;
    private String utilityBillImage;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String title;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String gender;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String nationality;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String residenceState;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String residenceCity;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String firstName;
    private String otherName;
//    @NotNull(message = "{field.not-null}")
//    @NotEmpty(message = "{field.not-empty}")
    private String dob;
    private String ippisNumber;
    private String retirementAge;
    private String spouseName;
    private String employmentStatus;
    private String employerName;
    private String employerAddress;
    private String employerCity;
    private String occupation;
    private String employmentDate;
    private String employmentNumber;
    private String loanType;
    private String kycTier;
}
