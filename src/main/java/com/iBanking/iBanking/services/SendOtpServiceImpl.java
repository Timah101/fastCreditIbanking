package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.payload.generics.DecryptRequest;
import com.iBanking.iBanking.payload.generics.EncryptResponse;
import com.iBanking.iBanking.payload.SendOtpPayloadRequest;
import com.iBanking.iBanking.payload.SendOtpResponsePayload;
import com.iBanking.iBanking.utils.AuthenticationApi;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.iBanking.iBanking.utils.ApiPaths.*;

@Slf4j
@Service
public class SendOtpServiceImpl implements SendOtpService {

    Gson gson = new Gson();

    @Autowired
    AuthenticationApi authenticationApi;


    @Override
    public SendOtpResponsePayload sendOtp(HttpSession session, String purpose, String mobileNumber) throws UnirestException {
        String accessToken = authenticationApi.getAccessToken();
        SendOtpResponsePayload sendOtp;
        SendOtpPayloadRequest requestPayload = new SendOtpPayloadRequest();

        requestPayload.setMobileNumber(mobileNumber);
        requestPayload.setPurpose(purpose);

        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponse encryptResponse1 = new EncryptResponse();
        encryptResponse1.setRequest(encryptResponsePayload);
        log.info("SEND OTP ENCRYPTION RESPONSE {}", encryptResponsePayload);


        String requestPayloadJsonString = gson.toJson(encryptResponse1);
        log.info("SEND OTP PAYLOAD  {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + SEND_OTP)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        log.info(" ERROR WHILE SENDING SEND OTP  {}", requestBody);
        if (jsonResponse.getStatus() != 200) {
            sendOtp = new SendOtpResponsePayload();
            sendOtp.setResponseCode("500");
            sendOtp.setResponseMessage("Error Occurred");
            log.info(" ERROR WHILE SENDING SEND OTP  {}", jsonResponse.getStatus());
            return sendOtp;
        }
        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequest decryptRequest = gson.fromJson(requestBody, DecryptRequest.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequest.getResponse());
        sendOtp = gson.fromJson(decrypt, SendOtpResponsePayload.class);

        //LOG REQUEST AND RESPONSE
        log.info("SEND OTP REQUEST PAYLOAD : {}", requestPayload);
        log.info("SEND OTP RESPONSE PAYLOAD : {}", gson.toJson(sendOtp));
        session.setAttribute("registerCustomerResponse", sendOtp);
        return sendOtp;
    }

}
