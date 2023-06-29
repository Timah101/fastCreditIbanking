package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.transactions.airtimeData.AirtimeRequestPayload;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.iBanking.iBanking.payload.generics.ResponseCodeResponseMessageResponsePayload;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataPlansRequestPayload;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataPlansResponsePayload;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataRequestPayload;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.iBanking.iBanking.utils.ApiPaths.*;
import static com.iBanking.iBanking.utils.AuthenticationApi.decryptPayload;
import static com.iBanking.iBanking.utils.AuthenticationApi.encryptPayload;

@Service
@Slf4j
public class AirtimeDataServiceImpl implements AirtimeDataService {

    Gson gson = new Gson();

    @Override
    public ResponseCodeResponseMessageResponsePayload airtimeTopUp(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        ResponseCodeResponseMessageResponsePayload airtimeTopUp;
        AirtimeRequestPayload requestPayload = new AirtimeRequestPayload();

        Forms airtimeForm = (Forms) session.getAttribute("airtimeForm");
        Forms airtimeFormPin = (Forms) session.getAttribute("airtimeFormPin");

        log.info("AIRTIME TOP UP FORMS AIRTIME D {}", airtimeForm);
        log.info("AIRTIME TOP UP FORMS PIN AIRTIME D {}", airtimeFormPin);

        requestPayload.setMobileNumber(airtimeForm.getMobileNumber());
        requestPayload.setDebitAccount(airtimeForm.getDebitAccount());
        requestPayload.setTelco(airtimeForm.getTelco());
        requestPayload.setAmount(airtimeForm.getAmount());
        requestPayload.setPin(airtimeFormPin.getPin());
        requestPayload.setChannelName("ibank");
        requestPayload.setToken("ttyuioknnnm");
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayloadJson);
        log.info("AIRTIME TOP UP ENCRYPTION RESPONSE {}", encryptResponsePayload);

        //CALL THE REGISTER CUSTOMER ENDPOINT
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload);
        log.info("AIRTIME TOP UP PAYLOAD D {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + AIRTIME_TOP_UP)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            airtimeTopUp = new ResponseCodeResponseMessageResponsePayload();
            airtimeTopUp.setResponseCode("500");
            airtimeTopUp.setResponseMessage("Error occurred, please try again");
            session.setAttribute("airtimeTopUpResponse", airtimeTopUp);
            log.info(" ERROR WHILE AIRTIME TOP UP {}", jsonResponse.getStatus());
            return airtimeTopUp;
        }
        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);

        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        airtimeTopUp = decryptPayload(decryptRequestPayload, ResponseCodeResponseMessageResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("AIRTIME TOP UP REQUEST PAYLOAD : {}", requestPayload);
        log.info("AIRTIME TOP UP RESPONSE PAYLOAD : {}", gson.toJson(airtimeTopUp));
        session.setAttribute("airtimeTopUpResponse", airtimeTopUp);
        return airtimeTopUp;
    }

    @Override
    public DataPlansResponsePayload dataPlans(HttpSession session, String telco) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        DataPlansResponsePayload dataPlans;
        DataPlansRequestPayload requestPayload = new DataPlansRequestPayload();

        requestPayload.setTelco(telco);

        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayloadJson);
        log.info("DATA PLANS LIST ENCRYPTION RESPONSE {}", encryptResponsePayload);

        //CALL THE REGISTER CUSTOMER ENDPOINT
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload);
        log.info("DATA PLANS LIST PAYLOAD {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + DATA_PLANS)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            dataPlans = new DataPlansResponsePayload();
            dataPlans.setResponseCode("500");

            session.setAttribute("dataPlansResponse", dataPlans);
            log.info(" ERROR WHILE GETTING DATA PLANS LIST {}", jsonResponse.getStatus());
            return dataPlans;
        }
        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);

        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        dataPlans = decryptPayload(decryptRequestPayload, DataPlansResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("DATA PLANS LIST REQUEST PAYLOAD : {}", requestPayload);
        log.info("DATA PLANS LIST RESPONSE PAYLOAD : {}", gson.toJson(dataPlans));
        session.setAttribute("dataPlansResponse", dataPlans);
        return dataPlans;
    }

    @Override
    public ResponseCodeResponseMessageResponsePayload dataTopUp(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        ResponseCodeResponseMessageResponsePayload dataTopUp;
        DataRequestPayload requestPayload = new DataRequestPayload();

        Forms dataForm = (Forms) session.getAttribute("dataForm");
        Forms dataFormPin = (Forms) session.getAttribute("dataFormPin");

        log.info("DATA TOP UP FORMS {}", dataForm);
        log.info("DATA TOP UP FORMS PIN D {}", dataFormPin);
        log.info("DATA PLAN FORMS PIN AIRTIME D {}", dataForm.getDataPlans());

        requestPayload.setMobileNumber(dataForm.getMobileNumber());
        requestPayload.setDebitAccount(dataForm.getDebitAccount());
        requestPayload.setTelco(dataForm.getTelco());
        requestPayload.setAmount(dataForm.getAmount());
        requestPayload.setPin(dataFormPin.getPin());
        requestPayload.setDataPlanId(dataForm.getDataPlans());
        requestPayload.setToken("ttyuioknnnm");
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayloadJson);
        log.info("DATA TOP UP ENCRYPTION RESPONSE {}", encryptResponsePayload);

        //CALL THE REGISTER CUSTOMER ENDPOINT
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload);
        log.info("DATA TOP UP PAYLOAD D {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + DATA_TOP_UP)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            dataTopUp = new ResponseCodeResponseMessageResponsePayload();
            dataTopUp.setResponseCode("500");
            dataTopUp.setResponseMessage("Error occurred, please try again");
            session.setAttribute("airtimeTopUpResponse", dataTopUp);
            log.info(" ERROR WHILE DATA TOP UP {}", jsonResponse.getStatus());
            return dataTopUp;
        }
        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);

        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        dataTopUp = decryptPayload(decryptRequestPayload, ResponseCodeResponseMessageResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("DATA TOP UP REQUEST PAYLOAD : {}", requestPayload);
        log.info("DATA TOP UP RESPONSE PAYLOAD : {}", gson.toJson(dataTopUp));
        session.setAttribute("dataTopUpResponse", dataTopUp);
        return dataTopUp;
    }
}
