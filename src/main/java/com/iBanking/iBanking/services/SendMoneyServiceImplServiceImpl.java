package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.SendMoneyForms;
import com.iBanking.iBanking.Forms.TransactionForms;
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

import java.util.ArrayList;
import java.util.List;

import static com.iBanking.iBanking.utils.ApiPaths.*;

@Service
@Slf4j
public class SendMoneyServiceImplServiceImpl implements SendMoneyService {
    Gson gson = new Gson();

    @Autowired
    AuthenticationApi authenticationApi;

    @Override
    public GeneralResponsePayload sendMoneyLocal(HttpSession session) {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            GeneralResponsePayload sendMoney;
            SendMoneyLocalRequestPayload requestPayload = new SendMoneyLocalRequestPayload();

            SendMoneyForms sendMoneyLocalForm = (SendMoneyForms) session.getAttribute("sendMoneyLocalForm");
            SendMoneyForms sendMoneyLocalFormPin = (SendMoneyForms) session.getAttribute("sendMoneyLocalFormPin");
            CustomerDetailsResponsePayload customerDetails = (CustomerDetailsResponsePayload) session.getAttribute("customerDetailsResponse");
            AccountDetailsResponsePayload accountDetails = (AccountDetailsResponsePayload) session.getAttribute("nameEnquiryLocalResponse");
            TransactionForms login = (TransactionForms) session.getAttribute("loginForm");
            String originatorName = customerDetails.getFirstName() + " " + customerDetails.getMiddleName() + " " + customerDetails.getLastName();
            requestPayload.setOriginatorName(originatorName);
            requestPayload.setMobileNumber(login.getMobileNumber());
            requestPayload.setAmount(sendMoneyLocalForm.getAmount());
            requestPayload.setCreditAccount(sendMoneyLocalForm.getCreditAccount());
            requestPayload.setDebitAccount(sendMoneyLocalForm.getDebitAccount());
            requestPayload.setSecurityAnswer("");
            requestPayload.setPin(sendMoneyLocalFormPin.getPin());
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
            log.info("SEND MONEY LOCAL ENCRYPTED REQUEST PAYLOAD : {}", requestPayloadJson);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + SEND_MONEY_OTHERS)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();

            log.info("SEND MONEY LOCAL RESPONSE PAYLOAD : {}", responseBody);

            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    sendMoney = gson.fromJson(decrypt, GeneralResponsePayload.class);
                } else {
                    sendMoney = new GeneralResponsePayload();
                    sendMoney.setResponseCode("199");
                    sendMoney.setResponseMessage("error occurred");
                }
                log.info("SEND MONEY LOCAL RESPONSE API : {}", decrypt);
            } else {
                sendMoney = new GeneralResponsePayload();
                sendMoney.setResponseCode("199");
                sendMoney.setResponseMessage("error occurred");
            }
            log.info("SEND MONEY LOCAL RESPONSE PAYLOAD : {}", gson.toJson(sendMoney));
            session.setAttribute("sendMoneyLocalResponse", sendMoney);
            return sendMoney;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public GeneralResponsePayload sendMoneyOthers(HttpSession session) throws UnirestException {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            GeneralResponsePayload sendMoney;
            SendMoneyOthersRequestPayload requestPayload = new SendMoneyOthersRequestPayload();

            SendMoneyForms sendMoneyOthersForm = (SendMoneyForms) session.getAttribute("sendMoneyOthersForm");
            SendMoneyForms sendMoneyOthersFormPin = (SendMoneyForms) session.getAttribute("sendMoneyOthersFormPin");

            OtherBanksNameEnquiryResponsePayload nameEnquiry = (OtherBanksNameEnquiryResponsePayload) session.getAttribute("otherBanksNameEnquiryResponse");

            TransactionForms login = (TransactionForms) session.getAttribute("loginForm");
            requestPayload.setMobileNumber(login.getMobileNumber());
//            requestPayload.setCre
            requestPayload.setDebitAccountNumber(sendMoneyOthersForm.getDebitAccount());
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
            log.info("SEND MONEY OTHERS REQUEST PAYLOAD  {}", requestPayloadJson);
            log.info("SEND MONEY OTHERS ENCRYPTED REQUEST PAYLOAD  {}", requestPayloadJsonString);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + SEND_MONEY_LOCAL)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();

            log.info("SEND MONEY OTHERS API RESPONSE PAYLOAD : {}", responseBody);

            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    sendMoney = gson.fromJson(decrypt, GeneralResponsePayload.class);
                } else {
                    sendMoney = new GeneralResponsePayload();
                    sendMoney.setResponseCode("199");
                    sendMoney.setResponseMessage("error occurred");
                }
                log.info("DECRYPTED SEND MONEY OTHERS RESPONSE API : {}", decrypt);
            } else {
                sendMoney = new GeneralResponsePayload();
                sendMoney.setResponseCode("199");
                sendMoney.setResponseMessage("error occurred");
            }
            log.info("SEND MONEY OTHERS RESPONSE PAYLOAD : {}", gson.toJson(sendMoney));
            session.setAttribute("sendMoneyOtherResponse", sendMoney);
            return sendMoney;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public GetBankListPResponsePayload getBankList(HttpSession session) throws UnirestException {
        try {
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
            log.info("GET BANKS LIST REQUEST PAYLOAD ENCRYPTED : {}", requestPayloadJsonString);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + GET_BANKS_LIST)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();

            log.info("GETTING BANK LIST API RESPONSE PAYLOAD : {}", responseBody);

            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    getBanksList = gson.fromJson(decrypt, GetBankListPResponsePayload.class);
                } else {
                    getBanksList = new GetBankListPResponsePayload();
                    GetBankList bankList = new GetBankList();
                    List<GetBankList> bankLists = new ArrayList<>();
                    bankLists.add(bankList);
                    getBanksList.setBankList(bankLists);
                    getBanksList.setResponseCode("99");
                    getBanksList.setResponseMessage("error occurred");
                }
                log.info("DECRYPTED GETTING BANK LIST RESPONSE API : {}", decrypt);
            } else {
                getBanksList = new GetBankListPResponsePayload();
                GetBankList bankList = new GetBankList();
                List<GetBankList> bankLists = new ArrayList<>();
                bankLists.add(bankList);
                getBanksList.setBankList(bankLists);
                getBanksList.setResponseCode("99");
                getBanksList.setResponseMessage("error occurred");
            }
            log.info("GETTING BANK LIST RESPONSE PAYLOAD : {}", gson.toJson(getBanksList));
            session.setAttribute("getBankListResponse", getBanksList);
            return getBanksList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public OtherBanksNameEnquiryResponsePayload otherBanksNameEnquiry(HttpSession session, String beneficiaryAccount, String beneficiaryBankCode) throws UnirestException {
        try {
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

            String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
            log.info("OTHER BANKS NAME ENQUIRY REQUEST PAYLOAD : {}", requestPayloadJson);
            log.info("OTHER BANKS NAME ENQUIRY ENCRYPT REQUEST PAYLOAD : {}", requestPayloadJsonString);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + OTHER_BANKS_NAME_ENQUIRY)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();

            log.info("OTHER BANKS NAME ENQUIRY API RESPONSE PAYLOAD : {}", responseBody);

            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    otherBanksNameEnquiry = gson.fromJson(decrypt, OtherBanksNameEnquiryResponsePayload.class);
                } else {
                    otherBanksNameEnquiry = new OtherBanksNameEnquiryResponsePayload();
                    otherBanksNameEnquiry.setResponseCode("199");
                    otherBanksNameEnquiry.setResponseDescription("error occurred");
                }
                log.info("DECRYPTED OTHER BANKS NAME ENQUIRY RESPONSE API : {}", decrypt);
            } else {
                otherBanksNameEnquiry = new OtherBanksNameEnquiryResponsePayload();
                otherBanksNameEnquiry.setResponseCode("199");
                otherBanksNameEnquiry.setResponseDescription("error occurred");
            }
            log.info("OTHER BANKS NAME ENQUIRY RESPONSE PAYLOAD : {}", gson.toJson(otherBanksNameEnquiry));
            session.setAttribute("otherBanksNameEnquiryResponse", otherBanksNameEnquiry);
            return otherBanksNameEnquiry;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
