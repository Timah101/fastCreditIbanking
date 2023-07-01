package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.iBanking.iBanking.payload.SendOtpPayloadRequest;
import com.iBanking.iBanking.payload.SendOtpResponsePayload;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.iBanking.iBanking.utils.ApiPaths.*;
import static com.iBanking.iBanking.utils.AuthenticationApi.*;

@Slf4j
@Service
public class SendOtpServiceImpl implements SendOtpService {

    Gson gson = new Gson();

    @Override
    public SendOtpResponsePayload sendOtp(HttpSession session, String purpose) throws UnirestException {
        String accessToken = getAccessToken();
        SendOtpResponsePayload sendOtp;
        SendOtpPayloadRequest requestPayload = new SendOtpPayloadRequest();

        String mobileNumber;
        Forms formRegister = (Forms) session.getAttribute("registerForm1");
        Forms formCreate = (Forms) session.getAttribute("createAccountForm1");
        System.out.println(formRegister + " : register" + formCreate + " : create");
        if (formRegister == null) {
            mobileNumber = formCreate.getMobileNumber();
        } else {
            mobileNumber = formRegister.getMobileNumber();
        }
        requestPayload.setMobileNumber(mobileNumber);
        requestPayload.setPurpose(purpose);

        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayloadJson);
        log.info("SEND OTP ENCRYPTION RESPONSE {}", encryptResponsePayload);

        //CALL THE REGISTER CUSTOMER ENDPOINT
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload);
        log.info("SEND OTP PAYLOAD  {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + SEND_OTP)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            sendOtp = new SendOtpResponsePayload();
//            customerDetailsResponse.setResponseCode("500");
//            customerDetailsResponse.setResponseMessage("Error Occured");
            log.info(" ERROR WHILE SENDING SEND OTP  {}", jsonResponse.getStatus());
            return sendOtp;
        }
        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);

        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        sendOtp = decryptPayload(decryptRequestPayload, SendOtpResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("SEND OTP REQUEST PAYLOAD : {}", requestPayload);
        log.info("SEND OTP RESPONSE PAYLOAD : {}", gson.toJson(sendOtp));
        session.setAttribute("registerCustomerResponse", sendOtp);
        return sendOtp;
    }
}
