package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.TransactionForms;
import com.iBanking.iBanking.payload.customer.LoginRequestPayload;
import com.iBanking.iBanking.payload.customer.LoginResponsePayload;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.iBanking.iBanking.utils.AuthenticationApi;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.iBanking.iBanking.utils.ApiPaths.*;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    Gson gson = new Gson();
    @Autowired
    LoginService loginService;
    @Autowired
    AuthenticationApi authenticationApi;

    @Override
    public LoginResponsePayload login(HttpSession session) throws UnirestException {
        String accessToken = authenticationApi.getAccessToken();
        session.setAttribute("accessToken", accessToken);
        LoginRequestPayload loginRequestPayload = new LoginRequestPayload();
        new LoginResponsePayload();
        LoginResponsePayload loginResponsePayload;
        TransactionForms loginForm = (TransactionForms) session.getAttribute("loginForm");
        loginRequestPayload.setMobileNumber(loginForm.getMobileNumber()); //
        loginRequestPayload.setAuthValue(loginForm.getPassword()); //
        loginRequestPayload.setDeviceId("dv123456");
        String requestPayload = gson.toJson(loginRequestPayload);
        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayload);
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);
        //CALL THE LOGIN ENDPOINT
        String requestPayloadJson = gson.toJson(encryptResponsePayload1);
        log.info("LOGIN DETAILS PAYLOAD {}", requestPayload);
        log.info("LOGIN DETAILS ENCRYPTED PAYLOAD {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CUSTOMER_AUTH)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJson).asString();
        String requestBody = jsonResponse.getBody();
        log.info(" LOGIN RESPONSE BODY {}", requestBody);
        if (jsonResponse.getStatus() != 200) {
            loginResponsePayload = new LoginResponsePayload();
//            customerDetailsResponse.setResponseCode("500");
//            customerDetailsResponse.setResponseMessage("Error Occured");
            log.info(" ERROR WHILE PROCESSING LOGIN {}", jsonResponse.getStatus());
            loginResponsePayload.setResponseCode("199");
            loginResponsePayload.setResponseMessage("Error occurred");
            session.setAttribute("loginResponse", loginResponsePayload);
            return loginResponsePayload;
        }

        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        loginResponsePayload = gson.fromJson(decrypt, LoginResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("LOGIN RESPONSE PAYLOAD : {}", gson.toJson(loginResponsePayload));
        session.setAttribute("loginResponse", loginResponsePayload);
        return loginResponsePayload;
    }

    public static void main(String[] args) throws UnirestException {
        LoginRequestPayload requestPayload = new LoginRequestPayload();
        LoginService loginService1 = new LoginServiceImpl();
//        loginService1.login(requestPayload);
    }
}
