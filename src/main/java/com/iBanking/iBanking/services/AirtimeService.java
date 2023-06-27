package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.generics.ResponseCodeResponseMessageResponsePayload;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface AirtimeService {

    ResponseCodeResponseMessageResponsePayload airtimeTopUp(HttpSession session) throws UnirestException;
}
