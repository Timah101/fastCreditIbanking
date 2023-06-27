package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.customer.CustomerDetailsResponsePayload;
import com.iBanking.iBanking.payload.customer.RegisterCustomerResponsePayload;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface CustomerService {
    CustomerDetailsResponsePayload getCustomerDetails(HttpSession httpSession) throws UnirestException;

    RegisterCustomerResponsePayload registerCustomer(HttpSession session) throws UnirestException;
}
