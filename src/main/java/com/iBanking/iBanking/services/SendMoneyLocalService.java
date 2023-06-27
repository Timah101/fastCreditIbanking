package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.generics.ResponseCodeResponseMessageResponsePayload;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface SendMoneyLocalService {

    ResponseCodeResponseMessageResponsePayload sendMoneyLocal(HttpSession session) throws UnirestException;

    ResponseCodeResponseMessageResponsePayload sendMoneyOthers(HttpSession session) throws UnirestException;

}
