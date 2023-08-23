package com.iBanking.iBanking.services;

import com.iBanking.iBanking.payload.SendOtpResponsePayload;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpSession;

public interface SendOtpService {

    SendOtpResponsePayload sendOtp(HttpSession session, String purpose, String mobileNumber) throws UnirestException;

}
