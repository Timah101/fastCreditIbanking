package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.generics.GeneralResponse;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataPlansResponsePayload;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface AirtimeDataService {

    GeneralResponse airtimeTopUp(HttpSession session) throws UnirestException;

    DataPlansResponsePayload dataPlans(HttpSession session, String telco) throws UnirestException;

    GeneralResponse dataTopUp(HttpSession session) throws UnirestException;
}
