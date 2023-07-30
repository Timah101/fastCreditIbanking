package com.iBanking.iBanking.services;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.generics.DecryptRequestPayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.iBanking.iBanking.payload.generics.GeneralResponsePayload;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataPlansList;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataPlansResponsePayload;
import com.iBanking.iBanking.payload.transactions.cableTv.*;
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
public class PayBillsServiceImpl implements PayBillsService {

    Gson gson = new Gson();
    @Autowired
    AuthenticationApi authenticationApi;

    @Override
    public GetCableTvBillersResponsePayload getCableTvBillers(HttpSession session, String biller) throws UnirestException {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            GetCableTvBillersResponsePayload cableTvBillers;
            GetCableTvBillersRequestPayload requestPayload = new GetCableTvBillersRequestPayload();

            requestPayload.setBiller(biller);

            String requestPayloadJson = gson.toJson(requestPayload);
            //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
            String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
            EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
            encryptResponsePayload1.setRequest(encryptResponsePayload);
            String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
            log.info("CABLE TV BILLERS LIST REQUEST PAYLOAD : {}", requestPayloadJson);
            log.info("CABLE TV BILLERS LIST ENCRYPTED REQUEST PAYLOAD : {}", requestPayloadJsonString);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CABLE_TV_BILLERS_LIST)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();
            log.info("CABLE TV LIST API RESPONSE PAYLOAD : {}", responseBody);
            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    cableTvBillers = gson.fromJson(decrypt, GetCableTvBillersResponsePayload.class);
                } else {
                    cableTvBillers = new GetCableTvBillersResponsePayload();
                    cableTvBillers.setResponseCode("199");
                    CableTvBillersList cableTvBillersList = new CableTvBillersList();
                    List<CableTvBillersList> billersLists = new ArrayList<>();
                    billersLists.add(cableTvBillersList);
                    cableTvBillers.setBillers(billersLists);
                }
                log.info("DECRYPTED CABLE TV PLANS LIST RESPONSE API : {}", decrypt);
            } else {
                cableTvBillers = new GetCableTvBillersResponsePayload();
                cableTvBillers.setResponseCode("199");
                CableTvBillersList cableTvBillersList = new CableTvBillersList();
                List<CableTvBillersList> billersLists = new ArrayList<>();
                billersLists.add(cableTvBillersList);
                cableTvBillers.setBillers(billersLists);
            }
            log.info("CABLE TV LIST RESPONSE PAYLOAD : {}", gson.toJson(cableTvBillers));
            session.setAttribute("cableTvBillersResponse", cableTvBillers);
            return cableTvBillers;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ValidateCableTvResponsePayload validateCableTv(HttpSession session, String biller, String cardNumber) throws UnirestException {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            ValidateCableTvResponsePayload validateCableTv;
            ValidateCableTvRequestPayload requestPayload = new ValidateCableTvRequestPayload();

            requestPayload.setBiller(biller);
            requestPayload.setCustomerNo(cardNumber);
            String requestPayloadJson = gson.toJson(requestPayload);
            //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
            String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
            EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
            encryptResponsePayload1.setRequest(encryptResponsePayload);
            //CALL THE GET BANKS ENDPOINT AND PASS THE ENCRYPTED PAYLOAD
            String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
            log.info("VALIDATE CABLE TV REQUEST PAYLOAD : {}", requestPayloadJson);
            log.info("VALIDATE CABLE TV ENCRYPTED REQUEST PAYLOAD : {}", requestPayloadJsonString);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + VALIDATE_CABLE_TV)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();

            log.info("VALIDATE CABLE API RESPONSE PAYLOAD : {}", responseBody);

            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    validateCableTv = gson.fromJson(decrypt, ValidateCableTvResponsePayload.class);
                } else {
                    validateCableTv = new ValidateCableTvResponsePayload();
                    validateCableTv.setResponseCode("199");
                    validateCableTv.setCardHolderName("error occurred");
                }
                log.info("DECRYPTED VALIDATE CABLE RESPONSE API : {}", decrypt);
            } else {
                validateCableTv = new ValidateCableTvResponsePayload();
                validateCableTv.setResponseCode("199");
                validateCableTv.setCardHolderName("error occurred");
            }
            log.info("VALIDATE CABLE RESPONSE PAYLOAD : {}", gson.toJson(validateCableTv));
            session.setAttribute("validateCableTvResponse", validateCableTv);
            return validateCableTv;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public CableTvPaymentResponse cableTvPayment(HttpSession session, String biller) throws UnirestException {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            CableTvPaymentResponse cableTvPayment = new CableTvPaymentResponse();
            CableTvPaymentRequestPayload requestPayload = new CableTvPaymentRequestPayload();

            Forms form;
            Forms formPin;
            String billerId;
            ValidateCableTvResponsePayload customerName;

            if (biller.equalsIgnoreCase("GOTV")) {
                form = (Forms) session.getAttribute("gotvForm");
                formPin = (Forms) session.getAttribute("gotvFormPin");
                customerName = (ValidateCableTvResponsePayload) session.getAttribute("validateCableTvResponse");
                billerId = form.getDataPlans();
            } else if (biller.equalsIgnoreCase("DSTV")) {
                form = (Forms) session.getAttribute("dstvForm");
                formPin = (Forms) session.getAttribute("dstvFormPin");
                customerName = (ValidateCableTvResponsePayload) session.getAttribute("validateCableTvResponse");
                billerId = form.getDataPlans();
            } else {
                form = new Forms();
                formPin = new Forms();
                billerId = "";
                customerName = new ValidateCableTvResponsePayload();
            }
            String billerIdSplitted = "";
            if (!billerId.isEmpty()) {
                String[] billerIdSplit = billerId.split(",");
                billerIdSplitted = billerIdSplit[2];
            }
            log.info("Biller Splitted {} ", form.getDataPlans());
            requestPayload.setBillerId(billerIdSplitted);
            requestPayload.setMobileNumber(form.getMobileNumber());
            requestPayload.setSmartCard(form.getSmartCardNumber());
            requestPayload.setDebitAccount(form.getDebitAccount());
            requestPayload.setAmount(form.getAmount());
            requestPayload.setCustomerName(String.valueOf(customerName));
            requestPayload.setPin(formPin.getPin());

            String requestPayloadJson = gson.toJson(requestPayload);

            //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
            String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
            EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
            encryptResponsePayload1.setRequest(encryptResponsePayload);


            String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
            log.info("CABLE PAYMENT REQUEST PAYLOAD : {}", requestPayloadJson);
            log.info("CABLE PAYMENT ENCRYPTED REQUEST PAYLOAD : {}", requestPayloadJsonString);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CABLE_TV_PAYMENT)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();

            log.info("CABLE PAYMENT RESPONSE PAYLOAD : {}", responseBody);

            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    cableTvPayment = gson.fromJson(decrypt, CableTvPaymentResponse.class);
                } else {
                    cableTvPayment = new CableTvPaymentResponse();
                    cableTvPayment.setResponseCode("199");
                    cableTvPayment.setResponseMessage("error occurred");
                }
                log.info("CABLE PAYMENT RESPONSE API : {}", decrypt);
            } else {
                cableTvPayment = new CableTvPaymentResponse();
                cableTvPayment.setResponseCode("199");
                cableTvPayment.setResponseMessage("error occurred");
            }
            log.info("CABLE PAYMENT RESPONSE PAYLOAD : {}", gson.toJson(cableTvPayment));
            session.setAttribute("cableTvPaymentResponse", cableTvPayment);
            return cableTvPayment;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public GetElectricityBillerResponsePayload getElectricityBillers(HttpSession session, String biller) throws UnirestException {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            GetElectricityBillerResponsePayload getElectricityBillers;
            GetElectricityBillerRequestPayload requestPayload = new GetElectricityBillerRequestPayload();
            requestPayload.setBiller("E02E");
            String requestPayloadJson = gson.toJson(requestPayload);
            //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
            String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
            EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
            encryptResponsePayload1.setRequest(encryptResponsePayload);
            //CALL THE GET BANKS ENDPOINT AND PASS THE ENCRYPTED PAYLOAD
            String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
            log.info("ELECTRICITY BILLERS LIST REQUEST PAYLOAD : {}", requestPayloadJson);
            log.info("ELECTRICITY BILLERS LIST REQUEST PAYLOAD : {}", requestPayloadJsonString);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + ELECTRICITY_BILLERS_LIST)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();

            log.info("ELECTRICITY BILLERS LIST API RESPONSE PAYLOAD : {}", responseBody);

            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    getElectricityBillers = gson.fromJson(decrypt, GetElectricityBillerResponsePayload.class);
                } else {
                    getElectricityBillers = new GetElectricityBillerResponsePayload();
                    ElectricityBillersList billersList = new ElectricityBillersList();
                    List<ElectricityBillersList> billersLists = new ArrayList<>();
                    billersLists.add(billersList);
                    getElectricityBillers.setResponseCode("199");
                    getElectricityBillers.setResponseMessage("error occurred");
                    getElectricityBillers.setBiller(billersLists);
                }
                log.info("ELECTRICITY BILLERS LIST RESPONSE API : {}", decrypt);
            } else {
                getElectricityBillers = new GetElectricityBillerResponsePayload();
                ElectricityBillersList billersList = new ElectricityBillersList();
                List<ElectricityBillersList> billersLists = new ArrayList<>();
                billersLists.add(billersList);
                getElectricityBillers.setResponseCode("199");
                getElectricityBillers.setResponseMessage("error occurred");
                getElectricityBillers.setBiller(billersLists);
            }
            log.info("ELECTRICITY BILLERS LIST RESPONSE PAYLOAD : {}", gson.toJson(getElectricityBillers));
            session.setAttribute("electricityBillersResponse", getElectricityBillers);
            return getElectricityBillers;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ValidateElectricityResponsePayload validateElectricity(HttpSession session, String biller, String meterNumber) throws UnirestException {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            ValidateElectricityResponsePayload validateElectricity;
            ValidateElectricityRequestPayload requestPayload = new ValidateElectricityRequestPayload();

            requestPayload.setBiller(biller);
            requestPayload.setMeterNumber(meterNumber);

            String requestPayloadJson = gson.toJson(requestPayload);

            //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
            String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
            EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
            encryptResponsePayload1.setRequest(encryptResponsePayload);

            String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
            log.info("VALIDATE ELECTRICITY REQUEST PAYLOAD : {}", requestPayloadJson);
            log.info("VALIDATE ELECTRICITY ENCRYPTED REQUEST PAYLOAD : {}", requestPayloadJson);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + VALIDATE_ELECTRICITY)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();

            log.info("VALIDATE ELECTRICITY RESPONSE PAYLOAD : {}", responseBody);

            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    validateElectricity = gson.fromJson(decrypt, ValidateElectricityResponsePayload.class);
                } else {
                    validateElectricity = new ValidateElectricityResponsePayload();
                    validateElectricity.setResponseCode("199");
                    validateElectricity.setResponseMessage("error occurred");
                }
                log.info("VALIDATE ELECTRICITY RESPONSE API : {}", decrypt);
            } else {
                validateElectricity = new ValidateElectricityResponsePayload();
                validateElectricity.setResponseCode("199");
                validateElectricity.setResponseMessage("error occurred");
            }
            log.info("VALIDATE ELECTRICITY RESPONSE PAYLOAD : {}", gson.toJson(validateElectricity));
            session.setAttribute("validateElectricityResponse", validateElectricity);
            return validateElectricity;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public GeneralResponsePayload electricityPayment(HttpSession session) throws UnirestException {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            GeneralResponsePayload electricityPayment = new GeneralResponsePayload();
            ElectricityPaymentRequestPayload requestPayload = new ElectricityPaymentRequestPayload();

            Forms form = (Forms) session.getAttribute("electricityForm");
            Forms formPin = (Forms) session.getAttribute("electricityFormPin");
            ValidateElectricityResponsePayload customerName = (ValidateElectricityResponsePayload) session.getAttribute("validateElectricityResponse");
            String billerId = "";
            if (form != null) {
                billerId = form.getElectricityBillerSelect().split(",")[0];
            }
            requestPayload.setBillerId(billerId);
            assert form != null;
            requestPayload.setMobileNumber(form.getMobileNumber());
            requestPayload.setMeterNumber(form.getSmartCardNumber());
            requestPayload.setDebitAccount(form.getDebitAccount());
            requestPayload.setAmount(form.getAmount());
            requestPayload.setCustomerName(customerName.getCardHolderName());
            requestPayload.setPin(formPin.getPin());

            String requestPayloadJson = gson.toJson(requestPayload);

            //Call the Encrypt ENDPOINT AND PASS THE PAYLOAD TO ENCRYPT
            String encryptResponsePayload = authenticationApi.encryptPayload(requestPayloadJson);
            EncryptResponsePayload encryptResponsePayload1 = new EncryptResponsePayload();
            encryptResponsePayload1.setRequest(encryptResponsePayload);

            String requestPayloadJsonString = gson.toJson(encryptResponsePayload1);
            log.info("ELECTRICITY PAYMENT REQUEST PAYLOAD : {}", requestPayloadJson);
            log.info("ELECTRICITY PAYMENT ENCRYPTED REQUEST PAYLOAD : {}", requestPayloadJsonString);
            HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + CABLE_TV_PAYMENT)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(requestPayloadJsonString).asString();
            String responseBody = jsonResponse.getBody();

            log.info("ELECTRICITY PAYMENT API RESPONSE PAYLOAD : {}", responseBody);

            if (jsonResponse.getStatus() == 200 && responseBody != null && responseBody.contains("response")) {
                DecryptRequestPayload decryptRequestPayload = gson.fromJson(responseBody, DecryptRequestPayload.class);
                String decrypt = authenticationApi.decryptPayload(decryptRequestPayload.getResponse());
                if (decrypt != null) {
                    electricityPayment = gson.fromJson(decrypt, GeneralResponsePayload.class);
                } else {
                    electricityPayment = new GeneralResponsePayload();
                    electricityPayment.setResponseCode("199");
                    electricityPayment.setResponseMessage("error occurred");
                }
                log.info("ELECTRICITY PAYMENT RESPONSE API : {}", decrypt);
            } else {
                electricityPayment = new GeneralResponsePayload();
                electricityPayment.setResponseCode("199");
                electricityPayment.setResponseMessage("error occurred");
            }
            log.info("ELECTRICITY PAYMENT RESPONSE PAYLOAD : {}", gson.toJson(electricityPayment));
            session.setAttribute("electricityPaymentResponse", electricityPayment);
            return electricityPayment;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
