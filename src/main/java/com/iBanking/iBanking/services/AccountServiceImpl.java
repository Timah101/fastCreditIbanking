package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.TransactionForms;
import com.iBanking.iBanking.payload.accout.AccountDetailsListResponsePayload;
import com.iBanking.iBanking.payload.accout.AccountDetailsRequestPayload;
import com.iBanking.iBanking.payload.accout.AccountDetailsResponsePayload;
import com.iBanking.iBanking.payload.accout.AccountList;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.iBanking.iBanking.payload.generics.MobileNumberRequestPayload;
import com.iBanking.iBanking.utils.AuthenticationApi;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import static com.iBanking.iBanking.utils.ApiPaths.*;


@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    Gson gson = new Gson();
    @Autowired
    AuthenticationApi authenticationApi;

    @Override
    public AccountDetailsResponsePayload getAccountDetailsLocal(HttpSession session, String accountNumber) {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            TransactionForms loginForm = (TransactionForms) session.getAttribute("loginForm");
            AccountDetailsResponsePayload accountDetailsResponse;
            AccountDetailsRequestPayload accountDetailsRequestPayload = new AccountDetailsRequestPayload();
            accountDetailsRequestPayload.setMobileNumber(loginForm.getMobileNumber());
            accountDetailsRequestPayload.setAccountNumber(accountNumber);
            accountDetailsRequestPayload.setDeviceId("dv123456");
            String requestPayload = gson.toJson(accountDetailsRequestPayload);

            //Call the Encrypt API
            String encryptResponsePayload = authenticationApi.encryptPayload(requestPayload);
            EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
            encryptResponsePayload1.setRequest(encryptResponsePayload);
            //CALL THE ACCOUNT DETAILS ENDPOINT
            String requestPayloadJson = gson.toJson(encryptResponsePayload1);
            log.info("LOCAL NAME ENQUIRY REQUEST PAYLOAD : {}", requestPayload);
            log.info("LOCAL NAME ENQUIRY ENCRYPTED REQUEST PAYLOAD : {}", requestPayloadJson);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + ACCOUNT_DETAILS)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJson).asString();
            String responseBody = jsonResponse.getBody();

            log.info("LOCAL NAME ENQUIRY API RESPONSE PAYLOAD : {}", responseBody);
            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    accountDetailsResponse = gson.fromJson(decrypt, AccountDetailsResponsePayload.class);
                } else {
                    accountDetailsResponse = new AccountDetailsResponsePayload();
                    accountDetailsResponse.setResponseCode("199");
                    accountDetailsResponse.setResponseMessage("error occurred");
                }
                log.info("DECRYPTED LOCAL NAME ENQUIRY RESPONSE API : {}", decrypt);
            } else {
                accountDetailsResponse = new AccountDetailsResponsePayload();
                accountDetailsResponse.setResponseCode("199");
                accountDetailsResponse.setResponseMessage("error occurred");
            }
            log.info("LOCAL NAME ENQUIRY RESPONSE PAYLOAD : {}", gson.toJson(accountDetailsResponse));
            session.setAttribute("nameEnquiryLocalResponse", accountDetailsResponse);
            return accountDetailsResponse;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public AccountDetailsListResponsePayload getAccountBalances(HttpSession session) {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            AccountDetailsListResponsePayload accountBalanceResponse;
            MobileNumberRequestPayload requestPayload = new MobileNumberRequestPayload();
            TransactionForms loginForm = (TransactionForms) session.getAttribute("loginForm");
            requestPayload.setMobileNumber(loginForm.getMobileNumber());
            String requestPayloadJson = gson.toJson(requestPayload);
            //Call the Encrypt API
            String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
            EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
            encryptResponsePayload1.setRequest(encryptResponsePayload);
            log.info("ACCOUNT DETAILS LIST REQUEST PAYLOAD : {}", requestPayloadJson);
            log.info("ACCOUNT DETAILS LIST ENCRYPTED REQUEST PAYLOAD : {}", encryptResponsePayload1);
            //CALL THE ACCOUNT DETAILS LIST ENDPOINT
            String requestString = gson.toJson(encryptResponsePayload1);
            log.info("ACCOUNT DETAILS LIST REQUEST PAYLOAD : {}", requestString);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + ACCOUNT_DETAILS_LIST)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestString).asString();
            String responseBody = jsonResponse.getBody();

            log.info("ACCOUNT DETAILS LIST API RESPONSE PAYLOAD : {}", responseBody);
            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    accountBalanceResponse = gson.fromJson(decrypt, AccountDetailsListResponsePayload.class);
                    if (accountBalanceResponse.getResponseCode().equals("03")) {
                        accountBalanceResponse = new AccountDetailsListResponsePayload();
                        AccountList accountList = new AccountList();
                        List<AccountList> accountListArrayList = new ArrayList<>();
                        accountListArrayList.add(accountList);
                        accountBalanceResponse.setResponseCode("00");
                        accountBalanceResponse.setAccountList(accountListArrayList);
                    }
                } else {
                    accountBalanceResponse = new AccountDetailsListResponsePayload();
                    AccountList accountList = new AccountList();
                    List<AccountList> accountListArrayList = new ArrayList<>();
                    accountListArrayList.add(accountList);
                    accountBalanceResponse.setAccountList(accountListArrayList);
                    accountBalanceResponse.setResponseCode("199");
                    accountBalanceResponse.setResponseMessage("error occurred");
                }
                log.info("DECRYPTED ACCOUNT DETAILS LIST RESPONSE API : {}", decrypt);
            } else {
                accountBalanceResponse = new AccountDetailsListResponsePayload();
                AccountList accountList = new AccountList();
                List<AccountList> accountListArrayList = new ArrayList<>();
                accountListArrayList.add(accountList);
                accountBalanceResponse.setAccountList(accountListArrayList);
                accountBalanceResponse.setResponseCode("199");
                accountBalanceResponse.setResponseMessage("error occurred");
            }
            log.info("ACCOUNT DETAILS LIST RESPONSE PAYLOAD : {}", gson.toJson(accountBalanceResponse));
            session.setAttribute("accountBalanceResponse", accountBalanceResponse);
            return accountBalanceResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
