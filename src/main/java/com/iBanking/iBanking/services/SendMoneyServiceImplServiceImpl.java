package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.transactions.SendMoneyLocalRequestPayload;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.iBanking.iBanking.payload.generics.ResponseCodeResponseMessageResponsePayload;
import com.iBanking.iBanking.payload.transactions.SendMoneyOthersRequestPayload;
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
public class SendMoneyServiceImplServiceImpl implements SendMoneyLocalService {
    Gson gson = new Gson();

    @Override
    public ResponseCodeResponseMessageResponsePayload sendMoneyLocal(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        ResponseCodeResponseMessageResponsePayload sendMoney;
        SendMoneyLocalRequestPayload requestPayload = new SendMoneyLocalRequestPayload();

        Forms airtimeForm = (Forms) session.getAttribute("airtimeForm");

        requestPayload.setOriginatorName("");
        requestPayload.setAmount("");
        requestPayload.setCreditAccount("");
        requestPayload.setDebitAccount("");
        requestPayload.setSecurityAnswer("");
        requestPayload.setMobileNumber("");
        requestPayload.setPin("");
        requestPayload.setBeneficiaryName("");
        requestPayload.setNarration("");
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayloadJson);
        log.info("SEND MONEY LOCAL ENCRYPTION RESPONSE {}", encryptResponsePayload);

        //CALL THE SEND MONEY ENDPOINT AND PASS THE ENCRYPTED PAYLOAD
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload);
        log.info("SEND MONEY LOCAL PAYLOAD D {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + SEND_MONEY_OTHERS)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            sendMoney = new ResponseCodeResponseMessageResponsePayload();

            log.info(" ERROR WHILE SEND MONEY LOCAL {}", jsonResponse.getStatus());
            return sendMoney;
        }
        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);

        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        sendMoney = decryptPayload(decryptRequestPayload, ResponseCodeResponseMessageResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("SEND MONEY LOCAL REQUEST PAYLOAD : {}", requestPayload);
        log.info("SEND MONEY LOCAL RESPONSE PAYLOAD : {}", gson.toJson(sendMoney));
        session.setAttribute("sendMoneyLocalResponse", sendMoney);
        return sendMoney;
    }

    @Override
    public ResponseCodeResponseMessageResponsePayload sendMoneyOthers(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        ResponseCodeResponseMessageResponsePayload sendMoney;
        SendMoneyOthersRequestPayload requestPayload = new SendMoneyOthersRequestPayload();

        Forms airtimeForm = (Forms) session.getAttribute("airtimeForm");

        requestPayload.setDestinationBankCode("");
        requestPayload.setBeneficiaryAccountNumber("");
        requestPayload.setAmount("");
        requestPayload.setBeneficiaryAccountName("");
        requestPayload.setBeneficiaryBvn("");
        requestPayload.setBeneficiaryKycLevel("");
        requestPayload.setDebitTheirRef("");
        requestPayload.setPaymentDetails("");
        requestPayload.setMobileNumber("");
        requestPayload.setPin("");
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayloadJson);
        log.info("SEND MONEY OTHERS ENCRYPTION RESPONSE {}", encryptResponsePayload);

        //CALL THE REGISTER CUSTOMER ENDPOINT
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload);
        log.info("SEND MONEY OTHERS PAYLOAD D {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + SEND_MONEY_LOCAL)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            sendMoney = new ResponseCodeResponseMessageResponsePayload();

            log.info(" ERROR WHILE SEND MONEY OTHERS {}", jsonResponse.getStatus());
            return sendMoney;
        }
        // PASS ENCRYPTED RESPONSE FROM CUSTOMER DETAILS TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);

        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        sendMoney = decryptPayload(decryptRequestPayload, ResponseCodeResponseMessageResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("SEND MONEY OTHERS REQUEST PAYLOAD : {}", requestPayload);
        log.info("SEND MONEY OTHERS RESPONSE PAYLOAD : {}", gson.toJson(sendMoney));
        session.setAttribute("sendMoneyOtherResponse", sendMoney);
        return sendMoney;
    }
}
