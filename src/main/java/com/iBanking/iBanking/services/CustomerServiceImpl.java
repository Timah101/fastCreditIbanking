package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.TransactionForms;
import com.iBanking.iBanking.payload.customer.*;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.iBanking.iBanking.payload.generics.GeneralResponsePayload;
import com.iBanking.iBanking.utils.AuthenticationApi;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import java.util.Map;

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
    public CustomerDetailsResponsePayload getCustomerDetails(HttpSession session, String mobileNUmber) throws UnirestException {
        CustomerDetailsResponsePayload customerDetailsResponse;
        try {
            String accessToken = authenticationApi.getAccessToken();
            session.setAttribute("accessTokenCustomer", accessToken);
            CustomerDetailsRequestPayload customerDetailsRequestPayload = new CustomerDetailsRequestPayload();
            customerDetailsRequestPayload.setMobileNumber(mobileNUmber);
            String requestPayload = gson.toJson(customerDetailsRequestPayload);

            //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
            String encryptResponsePayload = authenticationApi.encryptPayload(requestPayload);
            EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
            encryptResponsePayload1.setRequest(encryptResponsePayload);
            log.info("CUSTOMER DETAILS REQUEST PAYLOAD : {}", requestPayload);

            //CALL THE CUSTOMER DETAILS ENDPOINT
            String requestPayloadJson = gson.toJson(encryptResponsePayload1);

            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CUSTOMER_DETAILS)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJson).asString();
            String requestBody = jsonResponse.getBody();
            log.info("CUSTOMER DETAILS DECRYPTED RESPONSE PAYLOAD : {}", requestBody);
            if (jsonResponse.getStatus() != 200) {
                customerDetailsResponse = new CustomerDetailsResponsePayload();
                customerDetailsResponse.setResponseCode("99");
                customerDetailsResponse.setRegistered("99");
                customerDetailsResponse.setResponseMessage("Error Occurred");
                log.info(" ERROR WHILE GETTING CUSTOMER DETAILS {}", jsonResponse.getStatus());
                return customerDetailsResponse;
            }

            // PASS ENCRYPTED RESPONSE TO DECRYPT API
            DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
            String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
            customerDetailsResponse = gson.fromJson(decrypt, CustomerDetailsResponsePayload.class);
            //LOG REQUEST AND RESPONSE
            log.info("CUSTOMER DETAILS RESPONSE PAYLOAD : {}", gson.toJson(customerDetailsResponse));
            session.setAttribute("customerDetailsResponse", customerDetailsResponse);
            return customerDetailsResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            customerDetailsResponse = new CustomerDetailsResponsePayload();
            customerDetailsResponse.setResponseCode("99");
            customerDetailsResponse.setRegistered("99");
            customerDetailsResponse.setResponseMessage("Error Occurred");
            session.setAttribute("customerDetailsResponse", customerDetailsResponse);
            log.info(" ERROR WHILE GETTING CUSTOMER DETAILS {}", ex.getMessage());
            return customerDetailsResponse;
        }
    }

    @Override
    public RegisterCustomerResponsePayload registerCustomer(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessTokenCustomer");
        RegisterCustomerResponsePayload registerCustomer;
        RegisterCustomerRequestPayload requestPayload = new RegisterCustomerRequestPayload();
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
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);

        //CALL THE REGISTER CUSTOMER ENDPOINT
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
        log.info("REGISTER CUSTOMER REQUEST PAYLOAD : {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CUSTOMER_REGISTER)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        log.info("REGISTER CUSTOMER RESPONSE DECRYPTED PAYLOAD : {}", requestBody);
        if (jsonResponse.getStatus() != 200) {
            registerCustomer = new RegisterCustomerResponsePayload();
//            customerDetailsResponse.setResponseCode("500");
//            customerDetailsResponse.setResponseMessage("Error Occured");
            log.info(" ERROR WHILE REGISTERING CUSTOMER DETAILS {}", jsonResponse.getStatus());
            return registerCustomer;
        }
        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        registerCustomer = gson.fromJson(decrypt, RegisterCustomerResponsePayload.class);

        //LOG RESPONSE
        log.info("REGISTER CUSTOMER RESPONSE PAYLOAD : {}", gson.toJson(registerCustomer));
        session.setAttribute("registerCustomerResponse", registerCustomer);
        return registerCustomer;
    }

    @Override
    public CreateCustomerResponsePayload createCustomer(HttpSession session) throws UnirestException {
        String accessToken = authenticationApi.getAccessToken();
        CreateCustomerResponsePayload createCustomer;
        CreateCustomerWithoutBvnRequestPayload requestPayload = new CreateCustomerWithoutBvnRequestPayload();
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
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);

        //CALL THE CREATE CUSTOMER ENDPOINT
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
        log.info("CREATE CUSTOMER REQUEST PAYLOAD {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CUSTOMER_CREATE)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();

        log.info("CREATE CUSTOMER DECRYPTED RESPONSE PAYLOAD : {}", requestBody);
        if (jsonResponse.getStatus() != 200) {
            createCustomer = new CreateCustomerResponsePayload();
            createCustomer.setResponseCode("500");
            createCustomer.setResponseMessage("Error Occured");
            log.info(" ERROR WHILE CREATE CUSTOMER {}", jsonResponse.getStatus());
            session.setAttribute("registerCustomerResponse", createCustomer);
            return createCustomer;
        }

        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        createCustomer = gson.fromJson(decrypt, CreateCustomerResponsePayload.class);

        //LOG REQUEST AND RESPONSE
        log.info("CREATE CUSTOMER RESPONSE PAYLOAD : {}", gson.toJson(createCustomer));
        session.setAttribute("registerCustomerResponse", createCustomer);
        return createCustomer;
    }

    @Override
    public GeneralResponsePayload resetPassword(HttpSession session) throws UnirestException {
        String accessToken = authenticationApi.getAccessToken();
        session.setAttribute("accessTokenCustomer", accessToken);
        GeneralResponsePayload resetPassword;
        ResetPasswordRequestPayload requestPayload = new ResetPasswordRequestPayload();
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
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        log.info("PASSWORD RESET REQUEST PAYLOAD : {}", requestPayloadJson);
        //CALL THE CUSTOMER DETAILS ENDPOINT
        String requestPayloadString = gson.toJson(encryptResponsePayload1);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + PASSWORD_RESET)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            resetPassword = new GeneralResponsePayload();
            resetPassword.setResponseCode("99");

            resetPassword.setResponseMessage("99");
            log.info(" ERROR WHILE RESETTING PASSWORD {}", jsonResponse.getStatus());
            return resetPassword;
        }

        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        resetPassword = gson.fromJson(decrypt, GeneralResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("RESET PASSWORD RESPONSE PAYLOAD : {}", gson.toJson(resetPassword));
        session.setAttribute("resetPasswordResponse", resetPassword);
        return resetPassword;
    }

    @Override
    public GeneralResponsePayload resetPin(HttpSession session) throws UnirestException {
        String accessToken = authenticationApi.getAccessToken();
        session.setAttribute("accessTokenCustomer", accessToken);
        GeneralResponsePayload resetPin;
        ResetPasswordRequestPayload requestPayload = new ResetPasswordRequestPayload();
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
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        log.info("PIN RESET REQUEST PAYLOAD : {}", requestPayloadJson);
        //CALL THE CUSTOMER DETAILS ENDPOINT
        String requestPayloadString = gson.toJson(encryptResponsePayload1);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + PIN_RESET)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            resetPin = new GeneralResponsePayload();
            resetPin.setResponseCode("99");
            resetPin.setResponseMessage("error");
            log.info(" ERROR WHILE RESETTING PIN {}", jsonResponse.getStatus());
            session.setAttribute("resetPinResponse", resetPin);
            return resetPin;
        }

        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        resetPin = gson.fromJson(decrypt, GeneralResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("RESET PIN RESPONSE PAYLOAD : {}", gson.toJson(resetPin));
        session.setAttribute("resetPinResponse", resetPin);
        return resetPin;
    }

    @Override
    public GeneralResponsePayload updateCustomerDetails(HttpSession session, UpdateCustomerRequestPayload requestPayload) throws UnirestException {
        String accessToken = authenticationApi.getAccessToken();
        session.setAttribute("accessTokenCustomer", accessToken);
        GeneralResponsePayload updateCustomer;
        TransactionForms registerForm1 = (TransactionForms) session.getAttribute("registerForm1");
        requestPayload.setMobileNumber(registerForm1.getMobileNumber());
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);
        log.info("CUSTOMER UPDATE REQUEST PAYLOAD : {}", requestPayloadJson);
        //CALL THE CUSTOMER DETAILS ENDPOINT
        String requestPayloadString = gson.toJson(encryptResponsePayload1);
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
            updateCustomer = new GeneralResponsePayload();
            updateCustomer.setResponseCode("99");
            updateCustomer.setResponseMessage("error");
            log.info(" ERROR WHILE PROCESSING CUSTOMER UPDATE {}", jsonResponse.getStatus());
            session.setAttribute("updateCustomerResponse", updateCustomer);
            return updateCustomer;
        }

        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        updateCustomer = gson.fromJson(decrypt, GeneralResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("CUSTOMER UPDATE RESPONSE PAYLOAD : {}", gson.toJson(updateCustomer));
        session.setAttribute("updateCustomerResponse", updateCustomer);
        return updateCustomer;
    }

}
