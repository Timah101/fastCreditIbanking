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
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import static com.iBanking.iBanking.utils.ApiPaths.*;
import static com.iBanking.iBanking.utils.AuthenticationApi.decryptPayload;
import static com.iBanking.iBanking.utils.AuthenticationApi.encryptPayload;


@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    Gson gson = new Gson();

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
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayload);

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

        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        accountDetailsResponse = decryptPayload(decryptRequestPayload, AccountDetailsResponsePayload.class);

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
        EncryptResponsePayload encryptResponsePayload = encryptPayload(requestPayloadJson);
        log.info("ACCOUNT DETAILS LIST REQUEST PAYLOAD : {}", requestPayloadJson);
        //CALL THE ACCOUNT DETAILS LIST ENDPOINT
        String responseString = gson.toJson(encryptResponsePayload);
        HttpResponse<String> response = Unirest.post(BASE_URL + ACCOUNT_DETAILS_LIST)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(responseString).asString();
        String requestBody = response.getBody();
        if (response.getStatus() != 200 || response.getBody().isEmpty()) {
            accountBalanceResponse = new AccountDetailsListResponsePayload();
            AccountList balance = new AccountList();
            List<AccountList> bal = new ArrayList<>();
            balance.setAvailableBalance("...");
            balance.setLedgerBalance("...");
            balance.setAccountNumber("...");
            bal.add(balance);
            accountBalanceResponse.setAccountList(bal);
            accountBalanceResponse.setResponseCode("99");
            session.setAttribute("accountBalanceResponse", accountBalanceResponse);
            log.info(" ERROR WHILE GETTING ACCOUNT DETAILS LIST {}", response.getStatus());
            return accountBalanceResponse;
        }

        //CALL THE DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        decryptRequestPayload.setResponse(decryptRequestPayload.getResponse());
        accountBalanceResponse = decryptPayload(decryptRequestPayload, AccountDetailsListResponsePayload.class);
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
