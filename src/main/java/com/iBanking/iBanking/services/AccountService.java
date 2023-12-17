package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.accout.AccountDetailsList;
import com.iBanking.iBanking.payload.accout.AccountDetailsResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface AccountService {

    AccountDetailsResponse getAccountDetailsLocal(HttpSession session, String accountNumber) throws UnirestException;
    AccountDetailsList getAccountBalances(HttpSession session) throws UnirestException;

}
