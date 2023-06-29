package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.iBanking.iBanking.payload.transactions.cableTv.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.iBanking.iBanking.utils.ApiPaths.*;
import static com.iBanking.iBanking.utils.AuthenticationApi.decryptPayload;
import static com.iBanking.iBanking.utils.AuthenticationApi.encryptPayload;

@Slf4j
@Service
public class PayBillsServiceImpl implements PayBillsService {

    Gson gson = new Gson();

    @Override
    public GetCableTvBillersResponsePayload getCableTvBillers(HttpSession session, String biller) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        GetCableTvBillersResponsePayload cableTvBillers;
        GetCableTvBillersRequestPayload requestPayload = new GetCableTvBillersRequestPayload();

        requestPayload.setBiller(biller);

        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayloadJson);

        //CALL THE GET BANKS ENDPOINT AND PASS THE ENCRYPTED PAYLOAD
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload);
        log.info("CABLE TV BILLERS LIST REQUEST PAYLOAD : {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CABLE_TV_BILLERS_LIST)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            cableTvBillers = new GetCableTvBillersResponsePayload();

            log.info(" ERROR WHILE CABLE TV BILLERS LIST {}", jsonResponse.getStatus());
            return cableTvBillers;
        }
        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);

        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        cableTvBillers = decryptPayload(decryptRequestPayload, GetCableTvBillersResponsePayload.class);
        //LOG REQUEST AND RESPONSE

        log.info("CABLE TV BILLERS LIST RESPONSE PAYLOAD : {}", gson.toJson(cableTvBillers));
        session.setAttribute("cableTvBillersResponse", cableTvBillers);
        return cableTvBillers;
    }

    @Override
    public ValidateCableTvResponsePayload validateCableTv(HttpSession session, String biller, String cardNumber) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        ValidateCableTvResponsePayload validateCableTv;
        ValidateCableTvRequestPayload requestPayload = new ValidateCableTvRequestPayload();

        requestPayload.setBiller(biller);
        requestPayload.setCardNumber(cardNumber);

        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayloadJson);

        //CALL THE GET BANKS ENDPOINT AND PASS THE ENCRYPTED PAYLOAD
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload);
        log.info("VALIDATE CABLE TV REQUEST PAYLOAD : {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + VALIDATE_CABLE_TV)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            validateCableTv = new ValidateCableTvResponsePayload();

            log.info(" ERROR WHILE VALIDATE CABLE TV  {}", jsonResponse.getStatus());
            return validateCableTv;
        }
        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);

        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        validateCableTv = decryptPayload(decryptRequestPayload, ValidateCableTvResponsePayload.class);
        //LOG REQUEST AND RESPONSE

        log.info("VALIDATE CABLE TV RESPONSE PAYLOAD : {}", gson.toJson(validateCableTv));
        session.setAttribute("validateCableTvResponse", validateCableTv);
        return validateCableTv;
    }

    @Override
    public CableTvPaymentResponse cableTvPayment(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        CableTvPaymentResponse cableTvPayment = new CableTvPaymentResponse();
        CableTvPaymentRequestPayload requestPayload = new CableTvPaymentRequestPayload();


        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayloadJson);

        //CALL THE GET BANKS ENDPOINT AND PASS THE ENCRYPTED PAYLOAD
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload);
        log.info("CABLE PAYMENT REQUEST PAYLOAD : {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CABLE_TV_PAYMENT)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();

        if (jsonResponse.getStatus() != 200) {
//            cableTvPayment = new CableTvPaymentResponse();
            cableTvPayment.setResponseCode("00");
            cableTvPayment.setResponseMessage("Get Bills Payment Payload Tomorrow");
            session.setAttribute("cableTvPaymentResponse", cableTvPayment);
            log.info(" ERROR WHILE CABLE PAYMENT   {}", jsonResponse.getStatus());
            return cableTvPayment;
        }
        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);

        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        cableTvPayment = decryptPayload(decryptRequestPayload, CableTvPaymentResponse.class);
        //LOG REQUEST AND RESPONSE

        cableTvPayment.setResponseCode("00");
        cableTvPayment.setResponseMessage("Get Bills Payment Payload Tomorrow");

        log.info("CABLE PAYMENT RESPONSE PAYLOAD : {}", gson.toJson(cableTvPayment));
        session.setAttribute("cableTvPaymentResponse", cableTvPayment);
        return cableTvPayment;
    }
}
