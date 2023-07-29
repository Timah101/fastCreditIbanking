package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.iBanking.iBanking.payload.SendOtpPayloadRequest;
import com.iBanking.iBanking.payload.SendOtpResponsePayload;
import com.iBanking.iBanking.utils.AuthenticationApi;
import com.iBanking.iBanking.config.FastCreditConfig;
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
    FastCreditConfig fastCreditConfig;
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
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);
        log.info("SEND OTP ENCRYPTION RESPONSE {}", encryptResponsePayload);


        String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
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
        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        sendOtp = gson.fromJson(decrypt, SendOtpResponsePayload.class);

        //LOG REQUEST AND RESPONSE
        log.info("SEND OTP REQUEST PAYLOAD : {}", requestPayload);
        log.info("SEND OTP RESPONSE PAYLOAD : {}", gson.toJson(sendOtp));
        session.setAttribute("registerCustomerResponse", sendOtp);
        return sendOtp;
    }

    @Override
    public String testSecretKeys() {
        String userName = fastCreditConfig.userName();
        String password = fastCreditConfig.password();
        String encryptionKey = System.getenv("password");
        System.out.println("Encryption Key: " + encryptionKey);
        System.out.println("userName Key: " + userName);
        System.out.println("password Key: " + password);
        return userName;
    }
}
