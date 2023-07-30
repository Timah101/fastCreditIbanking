package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
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
import com.mashape.unirest.http.exceptions.UnirestException;
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
    public AccountDetailsResponsePayload getAccountDetailsLocal(HttpSession session, String accountNumber) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        Forms loginForm = (Forms) session.getAttribute("loginForm");
        AccountDetailsResponsePayload accountDetailsResponse;
        AccountDetailsRequestPayload accountDetailsRequestPayload = new AccountDetailsRequestPayload();
        accountDetailsRequestPayload.setMobileNumber(loginForm.getMobileNumber());
        accountDetailsRequestPayload.setAccountNumber(accountNumber);
        accountDetailsRequestPayload.setDeviceId("dv123456");
        String requestPayload = gson.toJson(accountDetailsRequestPayload);

        //Call the Encrypt API
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayload);

        //CALL THE ACCOUNT DETAILS ENDPOINT
        String requestPayloadJson = gson.toJson(encryptResponsePayload);
        log.info("ACCOUNT DETAILS REQUEST PAYLOAD : {}", requestPayloadJson);
        HttpResponse<String> response = Unirest.post(BASE_URL + ACCOUNT_DETAILS)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJson).asString();
        String requestBody = response.getBody();

        if (response.getStatus() != 200) {
            accountDetailsResponse = new AccountDetailsResponsePayload();
            log.info(" ERROR WHILE GETTING ACCOUNT DETAILS {}", response.getStatus());
            return accountDetailsResponse;
        }

        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        accountDetailsResponse = gson.fromJson(decrypt, AccountDetailsResponsePayload.class);
        if (response.getStatus() != 200 || response.getBody().isEmpty() || accountDetailsResponse == null) {
            accountDetailsResponse = new AccountDetailsResponsePayload();
            session.setAttribute("nameEnquiryLocalResponse", accountDetailsResponse);
            return accountDetailsResponse;
        }

        //LOG REQUEST RESPONSE
        log.info("ACCOUNT DETAILS RESPONSE PAYLOAD : {}", gson.toJson(accountDetailsResponse));
        session.setAttribute("nameEnquiryLocalResponse", accountDetailsResponse);
        return accountDetailsResponse;
    }

    @Override
    public AccountDetailsListResponsePayload getAccountBalances(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        AccountDetailsListResponsePayload accountBalanceResponse;
        MobileNumberRequestPayload requestPayload = new MobileNumberRequestPayload();
        Forms loginForm = (Forms) session.getAttribute("loginForm");
        requestPayload.setMobileNumber(loginForm.getMobileNumber());
        String requestPayloadJson = gson.toJson(requestPayload);
        //Call the Encrypt API
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);
        log.info("ACCOUNT DETAILS LIST REQUEST PAYLOAD : {}", requestPayloadJson);
        //CALL THE ACCOUNT DETAILS LIST ENDPOINT
        String requestString = gson.toJson(encryptResponsePayload1);
        log.info("ACCOUNT DETAILS LIST REQUEST PAYLOAD : {}", requestString);
        HttpResponse<String> response = Unirest.post(BASE_URL + ACCOUNT_DETAILS_LIST)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestString).asString();
        String requestBody = response.getBody();

        //CALL THE DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        accountBalanceResponse = gson.fromJson(decrypt, AccountDetailsListResponsePayload.class);
        if (response.getStatus() != 200 || response.getBody().isEmpty() || accountBalanceResponse == null) {
            accountBalanceResponse = new AccountDetailsListResponsePayload();
            AccountList balance = new AccountList();
            List<AccountList> bal = new ArrayList<>();
//            balance.setAvailableBalance("...");
//            balance.setLedgerBalance("...");
//            balance.setAccountNumber("...");
            bal.add(balance);
            accountBalanceResponse.setAccountList(bal);
            accountBalanceResponse.setResponseCode("99");
            session.setAttribute("accountBalanceResponse", accountBalanceResponse);
            log.info(" ERROR WHILE GETTING ACCOUNT DETAILS LIST {}", response.getStatus());
            return accountBalanceResponse;
        }
        if (accountBalanceResponse.getResponseCode().equals("03")) {
            accountBalanceResponse = new AccountDetailsListResponsePayload();
            AccountList balance = new AccountList();
            List<AccountList> bal = new ArrayList<>();
//            balance.setAvailableBalance("...");
//            balance.setLedgerBalance("...");
//            balance.setAccountNumber("...");
            bal.add(balance);
            accountBalanceResponse.setAccountList(bal);
            accountBalanceResponse.setResponseCode("99");
            session.setAttribute("accountBalanceResponse", accountBalanceResponse);
            log.info(" ACCOUNT DETAILS LIST IS EMPTY {}", response.getStatus());
            return accountBalanceResponse;
        }

        //LOG REQUEST RESPONSE
        log.info("ACCOUNT DETAILS LIST RESPONSE PAYLOAD : {}", gson.toJson(accountBalanceResponse));
        session.setAttribute("accountBalanceResponse", accountBalanceResponse);
        return accountBalanceResponse;
    }


    public static void main(String[] args) throws UnirestException {

        AccountService accountService = new AccountServiceImpl();
//        accountService.getAccountBalances();
//        customerService1.getCustomerDetails(se);
    }
}
