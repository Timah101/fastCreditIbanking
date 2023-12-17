package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.TransactionForms;
import com.iBanking.iBanking.payload.accout.AccountDetailsList;
import com.iBanking.iBanking.payload.accout.AccountDetailsRequest;
import com.iBanking.iBanking.payload.accout.AccountDetailsResponse;
import com.iBanking.iBanking.payload.accout.AccountList;
import com.iBanking.iBanking.payload.generics.DecryptRequest;
import com.iBanking.iBanking.payload.generics.EncryptResponse;
import com.iBanking.iBanking.payload.generics.MobileNumberRequest;
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
    public AccountDetailsResponse getAccountDetailsLocal(HttpSession session, String accountNumber) {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            TransactionForms loginForm = (TransactionForms) session.getAttribute("loginForm");
            AccountDetailsResponse accountDetailsResponse;
            AccountDetailsRequest accountDetailsRequestPayload = new AccountDetailsRequest();
            accountDetailsRequestPayload.setMobileNumber(loginForm.getMobileNumber());
            accountDetailsRequestPayload.setAccountNumber(accountNumber);
            accountDetailsRequestPayload.setDeviceId("dv123456");
            String requestPayload = gson.toJson(accountDetailsRequestPayload);

            //Call the Encrypt API
            String encryptResponsePayload = authenticationApi.encryptPayload(requestPayload);
            EncryptResponse encryptResponse1 = new EncryptResponse();
            encryptResponse1.setRequest(encryptResponsePayload);
            //CALL THE ACCOUNT DETAILS ENDPOINT
            String requestPayloadJson = gson.toJson(encryptResponse1);
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
                DecryptRequest decryptRequest = gson.fromJson(responseBody, DecryptRequest.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequest.getResponse());
                if (decrypt != null) {
                    accountDetailsResponse = gson.fromJson(decrypt, AccountDetailsResponse.class);
                } else {
                    accountDetailsResponse = new AccountDetailsResponse();
                    accountDetailsResponse.setResponseCode("199");
                    accountDetailsResponse.setResponseMessage("error occurred");
                }
                log.info("DECRYPTED LOCAL NAME ENQUIRY RESPONSE API : {}", decrypt);
            } else {
                accountDetailsResponse = new AccountDetailsResponse();
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
    public AccountDetailsList getAccountBalances(HttpSession session) {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            AccountDetailsList accountBalanceResponse;
            MobileNumberRequest requestPayload = new MobileNumberRequest();
            TransactionForms loginForm = (TransactionForms) session.getAttribute("loginForm");
            requestPayload.setMobileNumber("08076235565"); // loginForm.getMobileNumber()
            String requestPayloadJson = gson.toJson(requestPayload);
            //Call the Encrypt API
            String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
            EncryptResponse encryptResponse1 = new EncryptResponse();
            encryptResponse1.setRequest(encryptResponsePayload);
            log.info("ACCOUNT DETAILS LIST REQUEST PAYLOAD : {}", requestPayloadJson);
            log.info("ACCOUNT DETAILS LIST ENCRYPTED REQUEST PAYLOAD : {}", encryptResponse1);
            //CALL THE ACCOUNT DETAILS LIST ENDPOINT
            String requestString = gson.toJson(encryptResponse1);
            log.info("ACCOUNT DETAILS LIST REQUEST PAYLOAD : {}", requestString);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + ACCOUNT_DETAILS_LIST)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestString).asString();
            String responseBody = jsonResponse.getBody();

            log.info("ACCOUNT DETAILS LIST API RESPONSE PAYLOAD : {}", responseBody);
            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequest decryptRequest = gson.fromJson(responseBody, DecryptRequest.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequest.getResponse());
                if (decrypt != null) {
                    accountBalanceResponse = gson.fromJson(decrypt, AccountDetailsList.class);
                    if (accountBalanceResponse.getResponseCode().equals("03")) {
                        accountBalanceResponse = new AccountDetailsList();
                        AccountList accountList = new AccountList();
                        List<AccountList> accountListArrayList = new ArrayList<>();
                        accountList.setAccountName("");
                        accountList.setAccountNumber("");
//                        accountList.setAvailableBalance(Double.valueOf(""));
                        accountList.setStatus("");
                        accountListArrayList.add(accountList);
                        accountBalanceResponse.setResponseCode("00");
                        accountBalanceResponse.setAccountList(accountListArrayList);
                    }
                } else {
                    accountBalanceResponse = new AccountDetailsList();
                    AccountList accountList = new AccountList();
                    List<AccountList> accountListArrayList = new ArrayList<>();
                    accountList.setAccountName("");
                    accountList.setAccountNumber("");
//                    accountList.setAvailableBalance(Double.valueOf(""));
                    accountList.setStatus("");
                    accountListArrayList.add(accountList);
                    accountBalanceResponse.setAccountList(accountListArrayList);
                    accountBalanceResponse.setResponseCode("199");
                    accountBalanceResponse.setResponseMessage("error occurred");
                }
                log.info("DECRYPTED ACCOUNT DETAILS LIST RESPONSE API : {}", decrypt);
            } else {
                accountBalanceResponse = new AccountDetailsList();
                AccountList accountList = new AccountList();
                List<AccountList> accountListArrayList = new ArrayList<>();
                accountList.setAccountName("");
                accountList.setAccountNumber("");
//                accountList.setAvailableBalance(Double.valueOf(""));
                accountList.setStatus("");
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
