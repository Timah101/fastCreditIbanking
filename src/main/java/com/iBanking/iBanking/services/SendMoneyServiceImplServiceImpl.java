package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.accout.AccountDetailsResponsePayload;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponsePayload;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.iBanking.iBanking.payload.generics.GeneralResponsePayload;
import com.iBanking.iBanking.payload.transactions.sendMoney.*;
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
public class SendMoneyServiceImplServiceImpl implements SendMoneyService {
    Gson gson = new Gson();

    @Autowired
    AuthenticationApi authenticationApi;

    @Override
    public GeneralResponsePayload sendMoneyLocal(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        GeneralResponsePayload sendMoney;
        SendMoneyLocalRequestPayload requestPayload = new SendMoneyLocalRequestPayload();

        Forms sendMoneyLocalForm = (Forms) session.getAttribute("sendMoneyLocalForm");
        CustomerDetailsResponsePayload customerDetails = (CustomerDetailsResponsePayload) session.getAttribute("customerDetailsResponse");
        AccountDetailsResponsePayload accountDetails = (AccountDetailsResponsePayload) session.getAttribute("nameEnquiryLocalResponse");
        Forms login = (Forms) session.getAttribute("loginForm");
        String originatorName = customerDetails.getFirstName() + " " + customerDetails.getMiddleName() + " " + customerDetails.getLastName();
        requestPayload.setOriginatorName(originatorName);
        requestPayload.setMobileNumber(login.getMobileNumber());
        requestPayload.setAmount(sendMoneyLocalForm.getAmount());
        requestPayload.setCreditAccount(sendMoneyLocalForm.getCreditAccount());
        requestPayload.setDebitAccount(sendMoneyLocalForm.getDebitAccount());
        requestPayload.setSecurityAnswer("");
        requestPayload.setPin(sendMoneyLocalForm.getPin());
        requestPayload.setBeneficiaryName(accountDetails.getAccountName());
        requestPayload.setNarration(sendMoneyLocalForm.getNarration());
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);


        //CALL THE SEND MONEY ENDPOINT AND PASS THE ENCRYPTED PAYLOAD
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
        log.info("SEND MONEY LOCAL REQUEST PAYLOAD : {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + SEND_MONEY_OTHERS)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            sendMoney = new GeneralResponsePayload();

            log.info(" ERROR WHILE SEND MONEY LOCAL {}", jsonResponse.getStatus());
            return sendMoney;
        }
        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        sendMoney = gson.fromJson(decrypt, GeneralResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("SEND MONEY LOCAL RESPONSE PAYLOAD : {}", gson.toJson(sendMoney));
        session.setAttribute("sendMoneyLocalResponse", sendMoney);
        return sendMoney;
    }

    @Override
    public GeneralResponsePayload sendMoneyOthers(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        GeneralResponsePayload sendMoney;
        SendMoneyOthersRequestPayload requestPayload = new SendMoneyOthersRequestPayload();

        Forms sendMoneyOthersForm = (Forms) session.getAttribute("sendMoneyOthersForm");
        Forms sendMoneyOthersFormPin = (Forms) session.getAttribute("sendMoneyOthersFormPin");

        OtherBanksNameEnquiryResponsePayload nameEnquiry = (OtherBanksNameEnquiryResponsePayload) session.getAttribute("otherBanksNameEnquiryResponse");

        Forms login = (Forms) session.getAttribute("loginForm");
        requestPayload.setMobileNumber(login.getMobileNumber());
        requestPayload.setDestinationBankCode(nameEnquiry.getDestinationInstitutionCode());
        requestPayload.setBeneficiaryAccountNumber(sendMoneyOthersForm.getCreditAccount());
        requestPayload.setAmount(sendMoneyOthersForm.getAmount());
        requestPayload.setBeneficiaryAccountName(nameEnquiry.getAccountName());
        requestPayload.setBeneficiaryBvn(nameEnquiry.getBankVerificationNo());
        requestPayload.setBeneficiaryKycLevel(nameEnquiry.getKycLevel());
        requestPayload.setDebitTheirRef(nameEnquiry.getNameEnquiryRef());
        requestPayload.setPaymentDetails(sendMoneyOthersForm.getNarration());
        requestPayload.setPin(sendMoneyOthersFormPin.getPin());
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);

        String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
        log.info("SEND MONEY OTHERS PAYLOAD  {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + SEND_MONEY_LOCAL)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            sendMoney = new GeneralResponsePayload();

            log.info(" ERROR WHILE SEND MONEY OTHERS {}", jsonResponse.getStatus());
            return sendMoney;
        }
        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        sendMoney = gson.fromJson(decrypt, GeneralResponsePayload.class);
        //LOG REQUEST AND RESPONSE
        log.info("SEND MONEY OTHERS RESPONSE PAYLOAD : {}", gson.toJson(sendMoney));
        session.setAttribute("sendMoneyOtherResponse", sendMoney);
        return sendMoney;
    }

    @Override
    public GetBankListPResponsePayload getBankList(HttpSession session) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        GetBankListPResponsePayload getBanksList;
        GetBankListRequestPayload requestPayload = new GetBankListRequestPayload();

        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);

        //CALL THE GET BANKS ENDPOINT AND PASS THE ENCRYPTED PAYLOAD
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
        log.info("GET BANKS LIST REQUEST PAYLOAD : {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + GET_BANKS_LIST)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            getBanksList = new GetBankListPResponsePayload();
            session.setAttribute("getBankListResponse", getBanksList);
            log.info(" ERROR WHILE GETTING BANK LIST {}", jsonResponse.getStatus());
            return getBanksList;
        }
        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        getBanksList = gson.fromJson(decrypt, GetBankListPResponsePayload.class);
        //LOG REQUEST AND RESPONSE

        log.info("GET BANK LIST RESPONSE PAYLOAD : {}", gson.toJson(getBanksList));
        session.setAttribute("getBankListResponse", getBanksList);
        return getBanksList;
    }

    @Override
    public OtherBanksNameEnquiryResponsePayload otherBanksNameEnquiry(HttpSession session, String beneficiaryAccount, String beneficiaryBankCode) throws UnirestException {
        String accessToken = (String) session.getAttribute("accessToken");
        OtherBanksNameEnquiryResponsePayload otherBanksNameEnquiry;
        OtherBanksNameEnquiryRequestPayload requestPayload = new OtherBanksNameEnquiryRequestPayload();
        log.info("Bank code {}", beneficiaryBankCode);
        requestPayload.setAccountNumber(beneficiaryAccount);
        requestPayload.setDestinationInstitutionCode(beneficiaryBankCode);
        String requestPayloadJson = gson.toJson(requestPayload);

        //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
        String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
        EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
        encryptResponsePayload1.setRequest(encryptResponsePayload);

        //CALL THE GET BANKS ENDPOINT AND PASS THE ENCRYPTED PAYLOAD
        String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
        log.info("OTHER BANKS NAME ENQUIRY REQUEST PAYLOAD : {}", requestPayloadJson);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + OTHER_BANKS_NAME_ENQUIRY)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestPayloadJsonString).asString();
        String requestBody = jsonResponse.getBody();
        if (jsonResponse.getStatus() != 200) {
            otherBanksNameEnquiry = new OtherBanksNameEnquiryResponsePayload();

            log.info(" ERROR WHILE OTHER BANKS NAME ENQUIRY {}", jsonResponse.getStatus());
            return otherBanksNameEnquiry;
        }
        // PASS ENCRYPTED RESPONSE TO DECRYPT API
        DecryptRequestPayload decryptRequestPayload = gson.fromJson(requestBody, DecryptRequestPayload.class);
        String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
        otherBanksNameEnquiry = gson.fromJson(decrypt, OtherBanksNameEnquiryResponsePayload.class);
        //LOG REQUEST AND RESPONSE

        log.info("OTHER BANKS NAME ENQUIRY RESPONSE PAYLOAD : {}", gson.toJson(otherBanksNameEnquiry));
        session.setAttribute("otherBanksNameEnquiryResponse", otherBanksNameEnquiry);
        return otherBanksNameEnquiry;
    }
}
