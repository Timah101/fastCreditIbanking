package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.transactions.airtimeData.AirtimeRequestPayload;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.iBanking.iBanking.payload.generics.GeneralResponsePayload;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataPlansRequestPayload;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataPlansResponsePayload;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataRequestPayload;
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
public class AirtimeDataServiceImpl implements AirtimeDataService {

    Gson gson = new Gson();

    @Autowired
    AuthenticationApi authenticationApi;

    @Override
    public GeneralResponsePayload airtimeTopUp(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        GeneralResponsePayload airtimeTopUp;
        AirtimeRequestPayload requestPayload = new AirtimeRequestPayload();
        Forms airtimeForm = (Forms) session.getAttribute("airtimeForm");
        Forms airtimeFormPin = (Forms) session.getAttribute("airtimeFormPin");
        Forms loginForm = (Forms) session.getAttribute("loginForm");

        requestPayload.setMobileNumber(loginForm.getMobileNumber());
        requestPayload.setBeneficiaryMobileNumber(airtimeForm.getBeneficiaryMobileNumber());
        requestPayload.setDebitAccount(airtimeForm.getDebitAccount());
        requestPayload.setTelco(airtimeForm.getTelco());
        requestPayload.setAmount(airtimeForm.getAmount());
        requestPayload.setPin(airtimeFormPin.getPin());
        requestPayload.setChannelName("ibank");
        requestPayload.setToken("ttyuioknnnm");
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);

        //CALL THE REGISTER CUSTOMER ENDPOINT
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
        log.info("AIRTIME TOP UP REQUEST PAYLOAD : {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + AIRTIME_TOP_UP)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            airtimeTopUp = new GeneralResponsePayload();
            airtimeTopUp.setResponseCode("500");
            airtimeTopUp.setResponseMessage("Error occurred, please try again");
            session.setAttribute("airtimeTopUpResponse", airtimeTopUp);
            log.info(" ERROR WHILE AIRTIME TOP UP {}", jsonResponse.getStatus());
            return airtimeTopUp;
        }
        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        airtimeTopUp = gson.fromJson(decrypt, GeneralResponsePayload.class);
        //LOG REQUEST AND RESPONSE
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
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);
        //CALL THE DATA PLANS ENDPOINT
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
        log.info("DATA PLANS LIST REQUEST PAYLOAD : {}", requestPayloadJson);
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
        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        dataPlans = gson.fromJson(decrypt, DataPlansResponsePayload.class);
        //LOG REQUEST AND RESPONSE

        log.info("DATA PLANS LIST RESPONSE PAYLOAD : {}", gson.toJson(dataPlans));
        session.setAttribute("dataPlansResponse", dataPlans);
        return dataPlans;
    }

    @Override
    public GeneralResponsePayload dataTopUp(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        GeneralResponsePayload dataTopUp;
        DataRequestPayload requestPayload = new DataRequestPayload();
        Forms dataForm = (Forms) session.getAttribute("dataForm");
        Forms dataFormPin = (Forms) session.getAttribute("dataFormPin");
        Forms loginForm = (Forms) session.getAttribute("loginForm");

        String[] dataPlanId = new String[0];
        if (dataForm != null) {
            dataPlanId = dataForm.getDataPlans().split(",");
        }
        requestPayload.setMobileNumber(loginForm.getMobileNumber());
        assert dataForm != null;
        requestPayload.setBeneficiaryMobileNumber(dataForm.getBeneficiaryMobileNumber());
        requestPayload.setDebitAccount(dataForm.getDebitAccount());
        requestPayload.setTelco(dataForm.getTelco());
        requestPayload.setAmount(dataForm.getAmount());
        requestPayload.setPin(dataFormPin.getPin());
        requestPayload.setDataPlanId(dataPlanId[1]);
        requestPayload.setToken("ttyuioknnnm");
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);

        String requestPayloadJsonString = gson.toJson(encryptResponsePayload);
        log.info("DATA TOP UP REQUEST PAYLOAD : {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + DATA_TOP_UP)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            dataTopUp = new GeneralResponsePayload();
            dataTopUp.setResponseCode("500");
            dataTopUp.setResponseMessage("Error occurred, please try again");
            session.setAttribute("dataTopUpResponse", dataTopUp);
            log.info(" ERROR WHILE DATA TOP UP {}", jsonResponse.getStatus());
            return dataTopUp;
        }
        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        dataTopUp = gson.fromJson(decrypt, GeneralResponsePayload.class);
        //LOG REQUEST AND RESPONSE

        log.info("DATA TOP UP RESPONSE PAYLOAD : {}", gson.toJson(dataTopUp));
        session.setAttribute("dataTopUpResponse", dataTopUp);
        return dataTopUp;
    }
}
