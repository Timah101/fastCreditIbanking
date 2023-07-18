package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.customer.CreateCustomerResponsePayload;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponsePayload;
import com.iBanking.iBanking.payload.customer.RegisterCustomerResponsePayload;
import com.iBanking.iBanking.payload.generics.GeneralResponsePayload;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface CustomerService {
    CustomerDetailsResponsePayload getCustomerDetails(HttpSession httpSession, String mobileNumber) throws UnirestException;

    RegisterCustomerResponsePayload registerCustomer(HttpSession session) throws UnirestException;

    CreateCustomerResponsePayload createCustomer(HttpSession session) throws UnirestException;

    GeneralResponsePayload resetPassword(HttpSession session) throws UnirestException;
}
