package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.customer.LoginResponsePayload;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface LoginService {

    LoginResponsePayload login(HttpSession httpSession) throws UnirestException;
}
