package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.generics.GeneralResponsePayload;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataPlansResponsePayload;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface AirtimeDataService {

    GeneralResponsePayload airtimeTopUp(HttpSession session) throws UnirestException;

    DataPlansResponsePayload dataPlans(HttpSession session, String telco) throws UnirestException;

    GeneralResponsePayload dataTopUp(HttpSession session) throws UnirestException;
}
