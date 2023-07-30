package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.transactions.airtimeData.*;
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

import java.util.ArrayList;
import java.util.List;

import static com.iBanking.iBanking.utils.ApiPaths.*;

@Service
@Slf4j
public class AirtimeDataServiceImpl implements AirtimeDataService {

    Gson gson = new Gson();

    @Autowired
    AuthenticationApi authenticationApi;

    @Override
    public GeneralResponsePayload airtimeTopUp(HttpSession session) {
        try {
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

            String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
            log.info("AIRTIME TOP UP REQUEST PAYLOAD : {}", requestPayloadJson);
            log.info("AIRTIME TOP UP ENCRYPTED REQUEST PAYLOAD : {}", requestPayloadJsonString);

            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + AIRTIME_TOP_UP)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();

            log.info("AIRTIME TOP UP API RESPONSE PAYLOAD : {}", responseBody);

            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    airtimeTopUp = gson.fromJson(decrypt, GeneralResponsePayload.class);
                } else {
                    airtimeTopUp = new GeneralResponsePayload();
                    airtimeTopUp.setResponseCode("199");
                    airtimeTopUp.setResponseMessage("error occurred");
                }
                log.info("DECRYPTED AIRTIME TOP UP RESPONSE API : {}", decrypt);
            } else {
                airtimeTopUp = new GeneralResponsePayload();
                airtimeTopUp.setResponseCode("199");
                airtimeTopUp.setResponseMessage("error occurred");
            }
            log.info("AIRTIME TOP UP RESPONSE PAYLOAD : {}", gson.toJson(airtimeTopUp));
            session.setAttribute("airtimeTopUpResponse", airtimeTopUp);
            return airtimeTopUp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public DataPlansResponsePayload dataPlans(HttpSession session, String telco) {
        try {
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
            log.info("DATA PLANS LIST ENCRYPTED REQUEST PAYLOAD : {}", requestPayloadJsonString);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + DATA_PLANS)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();
            log.info("DATA PLANS LIST API RESPONSE PAYLOAD : {}", responseBody);
            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    dataPlans = gson.fromJson(decrypt, DataPlansResponsePayload.class);
                } else {
                    dataPlans = new DataPlansResponsePayload();
                    dataPlans.setResponseCode("199");
                    DataPlansList dataPlansList = new DataPlansList();
                    List<DataPlansList> dataPlansLists = new ArrayList<>();
                    dataPlansLists.add(dataPlansList);
                    dataPlans.setDataPlans(dataPlansLists);
                }
                log.info("DECRYPTED DATA PLANS LIST RESPONSE API : {}", decrypt);
            } else {
                dataPlans = new DataPlansResponsePayload();
                dataPlans.setResponseCode("199");
                DataPlansList dataPlansList = new DataPlansList();
                List<DataPlansList> dataPlansLists = new ArrayList<>();
                dataPlansLists.add(dataPlansList);
                dataPlans.setDataPlans(dataPlansLists);
            }
            log.info("DATA PLANS LIST RESPONSE PAYLOAD : {}", gson.toJson(dataPlans));
            session.setAttribute("dataPlansResponse", dataPlans);
            return dataPlans;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred");
        }
    }

    @Override
    public GeneralResponsePayload dataTopUp(HttpSession session) {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            GeneralResponsePayload dataTopUp;
            DataRequestPayload requestPayload = new DataRequestPayload();
            Forms dataForm = (Forms) session.getAttribute("dataForm");
            Forms dataFormPin = (Forms) session.getAttribute("dataFormPin");
            Forms loginForm = (Forms) session.getAttribute("loginForm");

            String[] dataPlanId = new String[4];
            if (dataForm != null) {
                if (dataForm.getDataPlans() != null) {
                    String[] dataFormSplit = dataForm.getDataPlans().split(",");
                    if (dataFormSplit.length > 1) {
                        dataPlanId = dataFormSplit;
                    }
                }
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
            log.info("DATA TOP UP ENCRYPTED REQUEST PAYLOAD : {}", requestPayloadJsonString);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + DATA_TOP_UP)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();
            log.info("DATA TOP UP API RESPONSE PAYLOAD : {}", responseBody);
            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    dataTopUp = gson.fromJson(decrypt, GeneralResponsePayload.class);
                } else {
                    dataTopUp = new GeneralResponsePayload();
                    dataTopUp.setResponseCode("199");
                    dataTopUp.setResponseMessage("error occurred");
                }
                log.info("DECRYPTED DATA TOP UP RESPONSE API : {}", decrypt);
            } else {
                dataTopUp = new GeneralResponsePayload();
                dataTopUp.setResponseCode("199");
                dataTopUp.setResponseMessage("error occurred");
            }
            log.info("DATA TOP UP RESPONSE PAYLOAD : {}", gson.toJson(dataTopUp));
            session.setAttribute("dataTopUpResponse", dataTopUp);
            return dataTopUp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred");
        }
    }
}
