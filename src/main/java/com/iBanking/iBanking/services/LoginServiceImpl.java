package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.customer.LoginRequestPayload;
import com.iBanking.iBanking.payload.customer.LoginResponsePayload;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.iBanking.iBanking.utils.ApiPaths.*;
import static com.iBanking.iBanking.utils.AuthenticationApi.*;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService{

    Gson gson = new Gson();
    @Autowired
    LoginService loginService;
    @Override
    public LoginResponsePayload login(HttpSession session) throws UnirestException {
        String accessToken = getAccessToken();
        session.setAttribute("accessToken", accessToken);
        LoginRequestPayload loginRequestPayload = new LoginRequestPayload();
        LoginResponsePayload loginResponsePayload = new LoginResponsePayload();
        Forms loginForm = (Forms) session.getAttribute("loginForm");
        loginRequestPayload.setMobileNumber(loginForm.getMobileNumber()); //
        loginRequestPayload.setAuthValue(loginForm.getPassword()); //
        loginRequestPayload.setDeviceId("dv123456");
        String requestPayload = gson.toJson(loginRequestPayload);
        log.info(" LOGIN REQUEST PAYLOAD : {}", requestPayload);
        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayload);


        //CALL THE LOGIN ENDPOINT
        String requestPayloadJson = gson.toJson(encryptResponsePayload);
        log.info("LOGIN DETAILS PAYLOAD {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CUSTOMER_LOGIN)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJson).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            loginResponsePayload = new LoginResponsePayload();
//            customerDetailsResponse.setResponseCode("500");
//            customerDetailsResponse.setResponseMessage("Error Occured");
            log.info(" ERROR WHILE GETTING PROCESSING LOGIN {}", jsonResponse.getStatus());
            return loginResponsePayload;
        }

        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);

        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        loginResponsePayload = decryptPayload(decryptRequestPayload, LoginResponsePayload.class);
        //LOG REQUEST AND RESPONSE

        log.info("LOGIN RESPONSE PAYLOAD : {}", gson.toJson(loginResponsePayload));
        return loginResponsePayload;
    }

    public static void main(String[] args) throws UnirestException {
        LoginRequestPayload requestPayload = new LoginRequestPayload();
        LoginService loginService1 = new LoginServiceImpl();
//        loginService1.login(requestPayload);
    }
}
