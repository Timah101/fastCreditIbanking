package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.customer.CustomerDetailsRequestPayload;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponsePayload;
import com.iBanking.iBanking.payload.customer.RegisterCustomerRequestPayload;
import com.iBanking.iBanking.payload.customer.RegisterCustomerResponsePayload;
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

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    Gson gson = new Gson();
    @Autowired
    CustomerService customerService;

    //GET CUSTOMER DETAILS
    @Override
    public CustomerDetailsResponsePayload getCustomerDetails(HttpSession session) throws UnirestException {
        String accessToken = getAccessToken();
        session.setAttribute("accessTokenCustomer", accessToken);
        CustomerDetailsResponsePayload customerDetailsResponse;
        CustomerDetailsRequestPayload customerDetailsRequestPayload = new CustomerDetailsRequestPayload();
        Forms loginForm = (Forms) session.getAttribute("loginForm");
        Forms registerForm = (Forms) session.getAttribute("registerForm1");
        String mobileNumber = "";
        if (loginForm == null) {
            mobileNumber = registerForm.getMobileNumber();
        } else {
            mobileNumber = loginForm.getMobileNumber();
        }

        customerDetailsRequestPayload.setMobileNumber(mobileNumber);
        String requestPayload = gson.toJson(customerDetailsRequestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayload);
        log.info("CUSTOMER DETAILS REQUEST PAYLOAD : {}", requestPayload);

        //CALL THE CUSTOMER DETAILS ENDPOINT
        String requestPayloadJson = gson.toJson(encryptResponsePayload);

        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CUSTOMER_DETAILS)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJson).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            customerDetailsResponse = new CustomerDetailsResponsePayload();
            customerDetailsResponse.setResponseCode("99");
            customerDetailsResponse.setRegistered("99");
            customerDetailsResponse.setResponseMessage("99");
            log.info(" ERROR WHILE GETTING CUSTOMER DETAILS {}", jsonResponse.getStatus());
            return customerDetailsResponse;
        }

        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);

        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        customerDetailsResponse = decryptPayload(decryptRequestPayload, CustomerDetailsResponsePayload.class);
        //LOG REQUEST AND RESPONSE

        log.info("CUSTOMER DETAILS RESPONSE PAYLOAD : {}", gson.toJson(customerDetailsResponse));
        session.setAttribute("customerDetailsResponse", customerDetailsResponse);
        return customerDetailsResponse;
    }

    @Override
    public RegisterCustomerResponsePayload registerCustomer(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessTokenCustomer");
        RegisterCustomerResponsePayload registerCustomer;
        RegisterCustomerRequestPayload requestPayload = new RegisterCustomerRequestPayload();
        Forms registerForm1 = (Forms) session.getAttribute("registerForm1");
        Forms registerForm2 = (Forms) session.getAttribute("registerForm2");
        Forms registerForm3 = (Forms) session.getAttribute("registerForm3");
        requestPayload.setMobileNumber(registerForm1.getMobileNumber());
        requestPayload.setSecurityQuestion(registerForm3.getSecurityQuestion());
        requestPayload.setSecurityAnswer(registerForm3.getSecurityAnswer());
        requestPayload.setPin(registerForm3.getPin());
        requestPayload.setPassword(registerForm3.getPassword());
        requestPayload.setOtp(registerForm2.getOtp());
        requestPayload.setReferredBy("");
        requestPayload.setReferralCode("");
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayloadJson);
        log.info("REGISTER CUSTOMER ENCRYPTION RESPONSE {}", encryptResponsePayload);

        //CALL THE REGISTER CUSTOMER ENDPOINT
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload);
        log.info("CUSTOMER DETAILS PAYLOAD D {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CUSTOMER_REGISTER)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            registerCustomer = new RegisterCustomerResponsePayload();
//            customerDetailsResponse.setResponseCode("500");
//            customerDetailsResponse.setResponseMessage("Error Occured");
            log.info(" ERROR WHILE REGISTERING CUSTOMER DETAILS {}", jsonResponse.getStatus());
            return registerCustomer;
        }
        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);

        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        registerCustomer = decryptPayload(decryptRequestPayload, RegisterCustomerResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("REGISTER CUSTOMER REQUEST PAYLOAD : {}", requestPayload);
        log.info("REGISTER CUSTOMER RESPONSE PAYLOAD : {}", gson.toJson(registerCustomer));
        session.setAttribute("registerCustomerResponse", registerCustomer);
        return registerCustomer;
    }

    public static void main(String[] args) throws UnirestException {

        CustomerService customerService1 = new CustomerServiceImpl();
        CustomerDetailsRequestPayload requestPayload = new CustomerDetailsRequestPayload();
//        customerService1.registerCustomer();
    }
}
