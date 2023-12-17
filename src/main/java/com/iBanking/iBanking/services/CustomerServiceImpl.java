package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.TransactionForms;
import com.iBanking.iBanking.payload.customer.*;
import com.iBanking.iBanking.payload.generics.DecryptRequest;
import com.iBanking.iBanking.payload.generics.EncryptResponse;
import com.iBanking.iBanking.payload.generics.GeneralResponse;
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
public class CustomerServiceImpl implements CustomerService {

    Gson gson = new Gson();
    @Autowired
    CustomerService customerService;
    @Autowired
    AuthenticationApi authenticationApi;

    //GET CUSTOMER DETAILS
    @Override
    public CustomerDetailsResponse getCustomerDetails(HttpSession session, String mobileNUmber) throws UnirestException {
        CustomerDetailsResponse customerDetailsResponse;
        try {
            String accessToken = authenticationApi.getAccessToken();
            session.setAttribute("accessTokenCustomer", accessToken);
            CustomerDetailsRequest customerDetailsRequest = new CustomerDetailsRequest();
            customerDetailsRequest.setMobileNumber(mobileNUmber);
            String requestPayload = gson.toJson(customerDetailsRequest);

            //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
            String encryptResponsePayload = authenticationApi.encryptPayload(requestPayload);
            EncryptResponse encryptResponse1 = new EncryptResponse();
            encryptResponse1.setRequest(encryptResponsePayload);
            log.info("CUSTOMER DETAILS REQUEST PAYLOAD : {}", requestPayload);

            //CALL THE CUSTOMER DETAILS ENDPOINT
            String requestPayloadJson = gson.toJson(encryptResponse1);

            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CUSTOMER_DETAILS)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJson).asString();
            String requestBody = jsonResponse.getBody();
            log.info("CUSTOMER DETAILS DECRYPTED RESPONSE PAYLOAD : {}", requestBody);
            if (jsonResponse.getStatus() != 200) {
                customerDetailsResponse = new CustomerDetailsResponse();
                customerDetailsResponse.setResponseCode("99");
                customerDetailsResponse.setRegistered("99");
                customerDetailsResponse.setResponseMessage("Error Occurred");
                log.info(" ERROR WHILE GETTING CUSTOMER DETAILS {}", jsonResponse.getStatus());
                return customerDetailsResponse;
            }

            // PASS ENCRYPTED RESPONSE TO DECRYPT API
            DecryptRequest decryptRequest = gson.fromJson(requestBody, DecryptRequest.class);
            String decrypt = authenticationApi.decryptPayload(decryptRequest.getResponse());
            customerDetailsResponse = gson.fromJson(decrypt, CustomerDetailsResponse.class);
            //LOG REQUEST AND RESPONSE
            log.info("CUSTOMER DETAILS RESPONSE PAYLOAD : {}", gson.toJson(customerDetailsResponse));
            session.setAttribute("customerDetailsResponse", customerDetailsResponse);
            return customerDetailsResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            customerDetailsResponse = new CustomerDetailsResponse();
            customerDetailsResponse.setResponseCode("99");
            customerDetailsResponse.setRegistered("99");
            customerDetailsResponse.setResponseMessage("Error Occurred");
            session.setAttribute("customerDetailsResponse", customerDetailsResponse);
            log.info(" ERROR WHILE GETTING CUSTOMER DETAILS {}", ex.getMessage());
            return customerDetailsResponse;
        }
    }

    @Override
    public RegisterCustomerResponse registerCustomer(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessTokenCustomer");
        RegisterCustomerResponse registerCustomer;
        RegisterCustomerRequest requestPayload = new RegisterCustomerRequest();
        TransactionForms registerForm1 = (TransactionForms) session.getAttribute("registerForm1");
        TransactionForms registerForm2 = (TransactionForms) session.getAttribute("registerForm2");
        TransactionForms registerForm3 = (TransactionForms) session.getAttribute("registerForm3");
        TransactionForms registerFormPassword = (TransactionForms) session.getAttribute("registerFormPassword");
        requestPayload.setMobileNumber(registerForm1.getMobileNumber());
        requestPayload.setSecurityQuestion(registerFormPassword.getSecurityQuestion());
        requestPayload.setSecurityAnswer(registerFormPassword.getSecurityAnswer());
        requestPayload.setPin(registerForm3.getPin());
        requestPayload.setPassword(registerFormPassword.getPassword());
        requestPayload.setOtp(registerForm2.getOtp());
        requestPayload.setReferredBy("");
        requestPayload.setReferralCode("");
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponse encryptResponse1 = new EncryptResponse();
        encryptResponse1.setRequest(encryptResponsePayload);

        //CALL THE REGISTER CUSTOMER ENDPOINT
        String requestPayloadJsonString = gson.toJson(encryptResponse1);
        log.info("REGISTER CUSTOMER REQUEST PAYLOAD : {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CUSTOMER_REGISTER)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        log.info("REGISTER CUSTOMER RESPONSE DECRYPTED PAYLOAD : {}", requestBody);
        if (jsonResponse.getStatus() != 200) {
            registerCustomer = new RegisterCustomerResponse();
//            customerDetailsResponse.setResponseCode("500");
//            customerDetailsResponse.setResponseMessage("Error Occured");
            log.info(" ERROR WHILE REGISTERING CUSTOMER DETAILS {}", jsonResponse.getStatus());
            return registerCustomer;
        }
        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequest decryptRequest = gson.fromJson(requestBody, DecryptRequest.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequest.getResponse());
        registerCustomer = gson.fromJson(decrypt, RegisterCustomerResponse.class);

        //LOG RESPONSE
        log.info("REGISTER CUSTOMER RESPONSE PAYLOAD : {}", gson.toJson(registerCustomer));
        session.setAttribute("registerCustomerResponse", registerCustomer);
        return registerCustomer;
    }

    @Override
    public CreateCustomerResponse createCustomer(HttpSession session) throws UnirestException {
        String accessToken = authenticationApi.getAccessToken();
        CreateCustomerResponse createCustomer;
        CreateCustomerWithoutBvnRequest requestPayload = new CreateCustomerWithoutBvnRequest();
        TransactionForms createForm1 = (TransactionForms) session.getAttribute("createAccountForm1");
        TransactionForms createForm2 = (TransactionForms) session.getAttribute("createAccountForm2");
        String passport = (String) session.getAttribute("passportSession");

        requestPayload.setMobileNumber(createForm1.getMobileNumber());
        requestPayload.setEmailAddress(createForm1.getEmail());
        requestPayload.setGender(createForm2.getGender());
        requestPayload.setFirstName(createForm2.getFirstName());
        requestPayload.setLastName(createForm2.getLastName());
        requestPayload.setMaritalStatus(createForm2.getMaritalStatus());
        requestPayload.setDob(createForm2.getDob());
        requestPayload.setPassportPhoto("passport");
        requestPayload.setIdType(createForm2.getIdType());
        requestPayload.setIdNumber(createForm2.getIdNumber());
        requestPayload.setSector("1000");
        requestPayload.setIndustry("1");


        String requestPayloadJson = gson.toJson(requestPayload);
        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponse encryptResponse1 = new EncryptResponse();
        encryptResponse1.setRequest(encryptResponsePayload);

        //CALL THE CREATE CUSTOMER ENDPOINT
        String requestPayloadJsonString = gson.toJson(encryptResponse1);
        log.info("CREATE CUSTOMER REQUEST PAYLOAD {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CUSTOMER_CREATE)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();

        log.info("CREATE CUSTOMER DECRYPTED RESPONSE PAYLOAD : {}", requestBody);
        if (jsonResponse.getStatus() != 200) {
            createCustomer = new CreateCustomerResponse();
            createCustomer.setResponseCode("500");
            createCustomer.setResponseMessage("Error Occured");
            log.info(" ERROR WHILE CREATE CUSTOMER {}", jsonResponse.getStatus());
            session.setAttribute("registerCustomerResponse", createCustomer);
            return createCustomer;
        }

        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequest decryptRequest = gson.fromJson(requestBody, DecryptRequest.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequest.getResponse());
        createCustomer = gson.fromJson(decrypt, CreateCustomerResponse.class);

        //LOG REQUEST AND RESPONSE
        log.info("CREATE CUSTOMER RESPONSE PAYLOAD : {}", gson.toJson(createCustomer));
        session.setAttribute("registerCustomerResponse", createCustomer);
        return createCustomer;
    }

    @Override
    public GeneralResponse resetPassword(HttpSession session) throws UnirestException {
        String accessToken = authenticationApi.getAccessToken();
        session.setAttribute("accessTokenCustomer", accessToken);
        GeneralResponse resetPassword;
        ResetPasswordRequest requestPayload = new ResetPasswordRequest();
        TransactionForms resetForm1 = (TransactionForms) session.getAttribute("resetForm1");
        TransactionForms resetOtp = (TransactionForms) session.getAttribute("resetOtpForm");
        TransactionForms resetForm2 = (TransactionForms) session.getAttribute("resetForm2");

        requestPayload.setMobileNumber(resetForm1.getMobileNumber());
        requestPayload.setOtp(resetOtp.getOtp());
        requestPayload.setSecurityQuestion(resetForm2.getSecurityQuestion());
        requestPayload.setSecurityAnswer(resetForm2.getSecurityAnswer());
        requestPayload.setNewPassword(resetForm2.getPassword());
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponse encryptResponse1 = new EncryptResponse();
        log.info("PASSWORD RESET REQUEST PAYLOAD : {}", requestPayloadJson);
        //CALL THE CUSTOMER DETAILS ENDPOINT
        String requestPayloadString = gson.toJson(encryptResponse1);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + PASSWORD_RESET)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            resetPassword = new GeneralResponse();
            resetPassword.setResponseCode("99");

            resetPassword.setResponseMessage("99");
            log.info(" ERROR WHILE RESETTING PASSWORD {}", jsonResponse.getStatus());
            return resetPassword;
        }

        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequest decryptRequest = gson.fromJson(requestBody, DecryptRequest.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequest.getResponse());
        resetPassword = gson.fromJson(decrypt, GeneralResponse.class);
        //LOG REQUEST AND RESPONSE
        log.info("RESET PASSWORD RESPONSE PAYLOAD : {}", gson.toJson(resetPassword));
        session.setAttribute("resetPasswordResponse", resetPassword);
        return resetPassword;
    }

    @Override
    public GeneralResponse resetPin(HttpSession session) throws UnirestException {
        String accessToken = authenticationApi.getAccessToken();
        session.setAttribute("accessTokenCustomer", accessToken);
        GeneralResponse resetPin;
        ResetPasswordRequest requestPayload = new ResetPasswordRequest();
        TransactionForms resetForm1 = (TransactionForms) session.getAttribute("pinResetForm1");
        TransactionForms resetOtp = (TransactionForms) session.getAttribute("resetOtpPinForm");
        TransactionForms resetForm2 = (TransactionForms) session.getAttribute("resetPinForm2");

        requestPayload.setMobileNumber(resetForm1.getMobileNumber());
        requestPayload.setOtp(resetOtp.getOtp());
        requestPayload.setSecurityQuestion(resetForm2.getSecurityQuestion());
        requestPayload.setSecurityAnswer(resetForm2.getSecurityAnswer());
        requestPayload.setNewPassword(resetForm2.getPin());
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponse encryptResponse1 = new EncryptResponse();
        log.info("PIN RESET REQUEST PAYLOAD : {}", requestPayloadJson);
        //CALL THE CUSTOMER DETAILS ENDPOINT
        String requestPayloadString = gson.toJson(encryptResponse1);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + PIN_RESET)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            resetPin = new GeneralResponse();
            resetPin.setResponseCode("99");
            resetPin.setResponseMessage("error");
            log.info(" ERROR WHILE RESETTING PIN {}", jsonResponse.getStatus());
            session.setAttribute("resetPinResponse", resetPin);
            return resetPin;
        }

        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequest decryptRequest = gson.fromJson(requestBody, DecryptRequest.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequest.getResponse());
        resetPin = gson.fromJson(decrypt, GeneralResponse.class);
        //LOG REQUEST AND RESPONSE
        log.info("RESET PIN RESPONSE PAYLOAD : {}", gson.toJson(resetPin));
        session.setAttribute("resetPinResponse", resetPin);
        return resetPin;
    }

    @Override
    public GeneralResponse updateCustomerDetails(HttpSession session, UpdateCustomerRequestPayload requestPayload) throws UnirestException {
        String accessToken = authenticationApi.getAccessToken();
        session.setAttribute("accessTokenCustomer", accessToken);
        GeneralResponse updateCustomer;
        TransactionForms registerForm1 = (TransactionForms) session.getAttribute("registerForm1");
        requestPayload.setMobileNumber(registerForm1.getMobileNumber());
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponse encryptResponse1 = new EncryptResponse();
        encryptResponse1.setRequest(encryptResponsePayload);
        log.info("CUSTOMER UPDATE REQUEST PAYLOAD : {}", requestPayloadJson);
        //CALL THE CUSTOMER DETAILS ENDPOINT
        String requestPayloadString = gson.toJson(encryptResponse1);
        log.info("CUSTOMER UPDATE ENCRYPTED REQUEST PAYLOAD : {}", requestPayloadString);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CUSTOMER_UPDATE)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadString).asString();
        String requestBody = jsonResponse.getBody();
        log.info("CUSTOMER UPDATE DECRYPTED RESPONSE PAYLOAD : {}", requestBody);
        log.info("CUSTOMER UPDATE DECRYPTED RESPONSE STATUS PAYLOAD : {}", jsonResponse.getStatus());
        if (jsonResponse.getStatus() != 200) {
            updateCustomer = new GeneralResponse();
            updateCustomer.setResponseCode("99");
            updateCustomer.setResponseMessage("error");
            log.info(" ERROR WHILE PROCESSING CUSTOMER UPDATE {}", jsonResponse.getStatus());
            session.setAttribute("updateCustomerResponse", updateCustomer);
            return updateCustomer;
        }

        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequest decryptRequest = gson.fromJson(requestBody, DecryptRequest.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequest.getResponse());
        updateCustomer = gson.fromJson(decrypt, GeneralResponse.class);
        //LOG REQUEST AND RESPONSE
        log.info("CUSTOMER UPDATE RESPONSE PAYLOAD : {}", gson.toJson(updateCustomer));
        session.setAttribute("updateCustomerResponse", updateCustomer);
        return updateCustomer;
    }

}
