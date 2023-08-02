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
        String accessToken = authenticationApi.getAccessToken();
        session.setAttribute("accessTokenCustomer", accessToken);
        CustomerDetailsResponsePayload customerDetailsResponse;
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
        if (jsonResponse.getStatus() != 200) {
            customerDetailsResponse = new CustomerDetailsResponsePayload();
            customerDetailsResponse.setResponseCode("99");
            customerDetailsResponse.setRegistered("99");
            customerDetailsResponse.setResponseMessage("99");
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
        TransactionForms createForm3 = (TransactionForms) session.getAttribute("createAccountForm3");
        TransactionForms createForm4 = (TransactionForms) session.getAttribute("createAccountForm4");
        String passport = (String) session.getAttribute("passportSession");
        String signature = (String) session.getAttribute("signatureSession");
        String utility = (String) session.getAttribute("utilitySession");
        requestPayload.setMobileNumber(createForm1.getMobileNumber());
        requestPayload.setEmailAddress(createForm1.getEmail());
        String createOtpLastPage = (String) session.getAttribute("createOtpLastPage");
        String createOtpFirstPage = (String) session.getAttribute("confirmOtp");
        String otp;
        System.out.println(" OTP IN FIRST PAGE : " + createOtpFirstPage);
        System.out.println(" OTP IN LAST PAGE : " + createOtpLastPage);
        if (createOtpLastPage.isEmpty()) {
            otp = createOtpFirstPage;
        } else {
            otp = createOtpLastPage;
        }
        requestPayload.setOtp(otp);
        requestPayload.setTitle(createForm2.getTitle());
        requestPayload.setGender(createForm2.getGender());
        requestPayload.setFirstName(createForm2.getFirstName());
        requestPayload.setLastName(createForm2.getLastName());
        requestPayload.setOtherName(createForm2.getOtherName());
        requestPayload.setMaritalStatus(createForm2.getMaritalStatus());
        requestPayload.setDob(createForm2.getDob());
        requestPayload.setNationality(createForm2.getNationality());
        requestPayload.setPassportPhoto(passport);
        requestPayload.setSignatureImage(signature);
        requestPayload.setIdType(createForm3.getIdType());
        requestPayload.setIdNumber(createForm3.getIdNumber());
        String utilityBill = "";
        if (utility != null) {
            utilityBill = "yes";
        }
        requestPayload.setUtilityBill(utilityBill);
        requestPayload.setUtilityBillImage(utility);
        requestPayload.setIppisNumber(createForm3.getIppisNumber());
        requestPayload.setResidenceState(createForm3.getResidenceState());
        requestPayload.setResidenceCity(createForm3.getResidenceCity());
        requestPayload.setEmploymentStatus(createForm4.getEmploymentStatus());
        requestPayload.setEmployerName(createForm4.getEmployerName());
        requestPayload.setEmployerAddress(createForm4.getEmployerAddress());
        requestPayload.setEmployerCity(createForm4.getEmployerCity());
        requestPayload.setOccupation(createForm4.getOccupation());
        requestPayload.setEmploymentDate(createForm4.getEmploymentDate());
        requestPayload.setRetirementAge(createForm4.getRetirementAge());
        requestPayload.setReferredBy(createForm4.getReferredBy());


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
        if (jsonResponse.getStatus() != 200) {
            createCustomer = new CreateCustomerResponsePayload();
//            customerDetailsResponse.setResponseCode("500");
//            customerDetailsResponse.setResponseMessage("Error Occured");
            log.info(" ERROR WHILE CREATE CUSTOMER {}", jsonResponse.getStatus());
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

    public static void main(String[] args) throws UnirestException {

        CustomerService customerService1 = new CustomerServiceImpl();
        CustomerDetailsRequestPayload requestPayload = new CustomerDetailsRequestPayload();
//        customerService1.registerCustomer();
    }
}
