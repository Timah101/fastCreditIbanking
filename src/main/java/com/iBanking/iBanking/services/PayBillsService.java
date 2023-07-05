package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.generics.GeneralResponsePayload;
import com.iBanking.iBanking.payload.transactions.cableTv.*;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface PayBillsService {

    GetCableTvBillersResponsePayload getCableTvBillers(HttpSession session, String biller) throws UnirestException;

    ValidateCableTvResponsePayload validateCableTv(HttpSession session, String biller, String cardNumber) throws UnirestException;

    CableTvPaymentResponse cableTvPayment(HttpSession session, String biller) throws UnirestException;

    GetElectricityBillerResponsePayload getElectricityBillers(HttpSession session, String biller) throws UnirestException;

    ValidateElectricityResponsePayload validateElectricity(HttpSession session, String biller, String meterNumber) throws UnirestException;

    GeneralResponsePayload electricityPayment(HttpSession session) throws UnirestException;
}
