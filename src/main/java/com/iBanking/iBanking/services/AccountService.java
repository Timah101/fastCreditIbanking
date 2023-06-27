package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.accout.AccountDetailsListResponsePayload;
import com.iBanking.iBanking.payload.accout.AccountDetailsResponsePayload;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface AccountService {

    AccountDetailsResponsePayload getAccountDetailsLocal(HttpSession session, String accountNumber) throws UnirestException;
    AccountDetailsListResponsePayload getAccountBalances(HttpSession session) throws UnirestException;
}
