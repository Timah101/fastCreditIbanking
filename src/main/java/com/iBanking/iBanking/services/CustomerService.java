package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.customer.CreateCustomerResponse;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponse;
import com.iBanking.iBanking.payload.customer.RegisterCustomerResponse;
import com.iBanking.iBanking.payload.customer.UpdateCustomerRequestPayload;
import com.iBanking.iBanking.payload.generics.GeneralResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface CustomerService {
    CustomerDetailsResponse getCustomerDetails(HttpSession httpSession, String mobileNumber) throws UnirestException;

    RegisterCustomerResponse registerCustomer(HttpSession session) throws UnirestException;

    CreateCustomerResponse createCustomer(HttpSession session) throws UnirestException;

    GeneralResponse resetPassword(HttpSession session) throws UnirestException;
    GeneralResponse resetPin(HttpSession session) throws UnirestException;
    GeneralResponse updateCustomerDetails(HttpSession session, UpdateCustomerRequestPayload requestPayload) throws UnirestException;
}
