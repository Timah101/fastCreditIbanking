package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.transactions.cableTv.CableTvPaymentResponse;
import com.iBanking.iBanking.payload.transactions.cableTv.GetCableTvBillersResponsePayload;
import com.iBanking.iBanking.payload.transactions.cableTv.ValidateCableTvResponsePayload;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface PayBillsService {

    GetCableTvBillersResponsePayload getCableTvBillers(HttpSession session, String biller) throws UnirestException;

    ValidateCableTvResponsePayload validateCableTv(HttpSession session, String biller, String cardNumber) throws UnirestException;

    CableTvPaymentResponse cableTvPayment(HttpSession session) throws UnirestException;
}
