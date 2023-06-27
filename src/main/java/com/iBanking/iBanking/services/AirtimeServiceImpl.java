package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.transactions.AirtimeRequestPayload;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.iBanking.iBanking.payload.generics.ResponseCodeResponseMessageResponsePayload;
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
public class AirtimeServiceImpl implements AirtimeService {

    Gson gson = new Gson();

    @Override
    public ResponseCodeResponseMessageResponsePayload airtimeTopUp(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        ResponseCodeResponseMessageResponsePayload airtimeTopUp;
        AirtimeRequestPayload requestPayload = new AirtimeRequestPayload();

        Forms airtimeForm = (Forms) session.getAttribute("airtimeForm");

        requestPayload.setMobileNumber(airtimeForm.getMobileNumber());
        requestPayload.setDebitAccount(airtimeForm.getDebitAccount());
        requestPayload.setTelco(airtimeForm.getTelco());
        requestPayload.setAmount(airtimeForm.getAmount());
        requestPayload.setPin(airtimeForm.getPin());
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
//        session.setAttribute("airtimeTopUpResponse", airtimeTopUp);
        return airtimeTopUp;
    }
}
