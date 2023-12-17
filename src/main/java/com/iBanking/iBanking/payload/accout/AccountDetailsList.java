package com.iBanking.iBanking.payload.accout;

import lombok.Data;

import java.util.List;

@Data
public class AccountDetailsList {

    private List<AccountList> accountList;
    private String responseCode;
    private String responseMessage;
}
