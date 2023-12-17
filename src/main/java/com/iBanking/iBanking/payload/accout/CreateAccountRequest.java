package com.iBanking.iBanking.payload.accout;

import lombok.Data;

@Data
public class CreateAccountRequest {
    private String mobileNumber;
    private String customerNumber;
    private String branchCode = "NG0010001";
    private String accountOfficer;
    private String accountName;
    private String category;
    private String requestId;
}
