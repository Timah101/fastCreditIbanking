package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.generics.GeneralResponse;
import com.iBanking.iBanking.payload.transactions.sendMoney.GetBankListPResponsePayload;
import com.iBanking.iBanking.payload.transactions.sendMoney.OtherBanksNameEnquiryResponsePayload;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface SendMoneyService {

    GeneralResponse sendMoneyLocal(HttpSession session) throws UnirestException;

    GeneralResponse sendMoneyOthers(HttpSession session) throws UnirestException;

    GetBankListPResponsePayload getBankList(HttpSession session) throws UnirestException;

    OtherBanksNameEnquiryResponsePayload otherBanksNameEnquiry(HttpSession session, String beneficiaryAccount, String beneficiaryBankCode) throws UnirestException;

}
