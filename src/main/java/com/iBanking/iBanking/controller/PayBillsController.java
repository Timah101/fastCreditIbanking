package com.iBanking.iBanking.controller;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.TransactionForms;
import com.iBanking.iBanking.payload.accout.AccountDetailsListResponsePayload;
import com.iBanking.iBanking.payload.generics.GeneralResponsePayload;
import com.iBanking.iBanking.payload.transactions.cableTv.*;
import com.iBanking.iBanking.services.PayBillsService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
public class PayBillsController {

    @Autowired
    PayBillsService payBillsService;

    @GetMapping("/pay-bills")
    public String showPayBills(Model model, HttpSession session) throws UnirestException {


        GetCableTvBillersResponsePayload gotvBillerPlan = (GetCableTvBillersResponsePayload) session.getAttribute("cableTvBillersResponse");
        GetCableTvBillersResponsePayload dstvBillerPlan = (GetCableTvBillersResponsePayload) session.getAttribute("cableTvBillersResponse");
        GetElectricityBillerResponsePayload electricityBiller = (GetElectricityBillerResponsePayload) session.getAttribute("electricityBillersResponse");
//                payBillsService.getCableTvBillers(session, gotvBiller);

//                payBillsService.getCableTvBillers(session, dstvBiller);

//                payBillsService.getElectricityBillers(session, biller);
        String selectedOptionGotv = "";
        String selectedOptionDstv = "";
        model.addAttribute("selectedOptionGotv", selectedOptionGotv);
        model.addAttribute("selectedOptionDstv", selectedOptionDstv);

        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("gotvBillerPlan", gotvBillerPlan.getBillers());
        model.addAttribute("dstvBillerPlan", dstvBillerPlan.getBillers());
        model.addAttribute("electricityBillers", electricityBiller.getBiller());
        model.addAttribute("gotvForm", new TransactionForms());
        model.addAttribute("gotvFormPin", new TransactionForms());
        model.addAttribute("dstvForm", new TransactionForms());
        model.addAttribute("dstvFormPin", new TransactionForms());
        model.addAttribute("electricityForm", new TransactionForms());
        model.addAttribute("electricityFormPin", new TransactionForms());

        return "transactions/pay-bills";
    }

    @PostMapping("/gotv-form")
    public String processGotvForm(@ModelAttribute("gotvForm") TransactionForms gotvForm, Model model, HttpSession session) throws UnirestException {
        session.setAttribute("gotvForm", gotvForm);

        GetCableTvBillersResponsePayload gotvBillerPlan = (GetCableTvBillersResponsePayload) session.getAttribute("cableTvBillersResponse");
        GetCableTvBillersResponsePayload dstvBillerPlan = (GetCableTvBillersResponsePayload) session.getAttribute("cableTvBillersResponse");
        GetElectricityBillerResponsePayload electricityBiller = (GetElectricityBillerResponsePayload) session.getAttribute("electricityBillersResponse");

        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("gotvBillerPlan", gotvBillerPlan.getBillers());
        model.addAttribute("dstvBillerPlan", dstvBillerPlan.getBillers());
        model.addAttribute("electricityBillers", electricityBiller.getBiller());
        model.addAttribute("gotvForm", new TransactionForms());
        model.addAttribute("gotvFormPin", new TransactionForms());
        model.addAttribute("dstvForm", new TransactionForms());
        model.addAttribute("dstvFormPin", new TransactionForms());
        model.addAttribute("electricityForm", new TransactionForms());
        model.addAttribute("electricityFormPin", new TransactionForms());

        TransactionForms gotvTxnData = (TransactionForms) session.getAttribute("gotvForm");
        String bouquet = "";
        if (gotvTxnData != null) {
            String[] bouquetSplitted = gotvTxnData.getDataPlans().split(",");
            bouquet = bouquetSplitted[3];
        }

        model.addAttribute("bouquet", bouquet);
        model.addAttribute("gotvTxnData", gotvTxnData);
        model.addAttribute("submitted", true);

        return "transactions/pay-bills";
    }

    @PostMapping("/gotv")
    @ResponseBody
    public String processGotv(Model model, TransactionForms gotvFormPin, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("gotvFormPin", gotvFormPin);
        String biller = "GOTV";
        payBillsService.cableTvPayment(session, biller);
        CableTvPaymentResponse cableTvPaymentResponse = (CableTvPaymentResponse) session.getAttribute("cableTvPaymentResponse");
        if (cableTvPaymentResponse.getResponseCode().equals("00")) {
            return "00";
        } else {
//            model.addAttribute("submitted", true);
            String customErrorMessage = cableTvPaymentResponse.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return cableTvPaymentResponse.getResponseMessage();
        }

    }


    //GOTV NAME ENQUIRY VIA AJAX
    @GetMapping({"/validate/gotv/{cardNumber}"})
    @ResponseBody
    public String validateGotv(@PathVariable("cardNumber") String cardNumber, Model model, HttpSession session) throws UnirestException {
        String biller = "GOTV";
        ValidateCableTvResponsePayload validateCableTv = payBillsService.validateCableTv(session, biller, cardNumber);
        if (validateCableTv.getResponseCode().equals("00")) {
            log.info("NAME ENQUIRY GOTV {}", validateCableTv);
            String name = validateCableTv.getCardholderName();
            model.addAttribute("name", name);
            return name;
        } else {

            return "invalid";
        }

    }

    @GetMapping({"/data/{biller}"})
    @ResponseBody
    public String gotvPlans(@PathVariable("biller") String telco, HttpSession session) throws UnirestException {
        Gson gson = new Gson();
        GetCableTvBillersResponsePayload gotvPlans = payBillsService.getCableTvBillers(session, telco);

        if (gotvPlans.getBillers().isEmpty() || !gotvPlans.getResponseCode().equals("00")) {
            return "error fetching data";
        }
        return gson.toJson(gotvPlans);
    }


    //COLLECT INFORMATION FOR DSTV AND ROUTE TO CONFIRM TRANSACTION
    @PostMapping("/dstv-form")
    public String processDstvForm(@ModelAttribute("dstvForm") TransactionForms gotvForm, Model model, HttpSession session) throws UnirestException {
        session.setAttribute("dstvForm", gotvForm);

        String gotvBiller = "GOTV";
        String dstvBiller = "DSTV";
        String biller = "E02E";
        GetCableTvBillersResponsePayload gotvBillerPlan = (GetCableTvBillersResponsePayload) session.getAttribute("cableTvBillersResponse");
        GetCableTvBillersResponsePayload dstvBillerPlan = (GetCableTvBillersResponsePayload) session.getAttribute("cableTvBillersResponse");
        GetElectricityBillerResponsePayload electricityBiller = (GetElectricityBillerResponsePayload) session.getAttribute("electricityBillersResponse");
        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("gotvBillerPlan", gotvBillerPlan.getBillers());
        model.addAttribute("dstvBillerPlan", dstvBillerPlan.getBillers());
        model.addAttribute("electricityBillers", electricityBiller.getBiller());
        model.addAttribute("gotvForm", new TransactionForms());
        model.addAttribute("gotvFormPin", new TransactionForms());
        model.addAttribute("dstvForm", new TransactionForms());
        model.addAttribute("dstvFormPin", new TransactionForms());
        model.addAttribute("electricityForm", new TransactionForms());
        model.addAttribute("electricityFormPin", new TransactionForms());

        TransactionForms dstvTxnData = (TransactionForms) session.getAttribute("dstvForm");

        String bouquet = "";
        if (dstvTxnData != null) {
            String[] bouquetSplit = dstvTxnData.getDataPlans().split(",");
            bouquet = bouquetSplit[3];
        }

        model.addAttribute("bouquet", bouquet);
        model.addAttribute("dstvTxnData", dstvTxnData);

        model.addAttribute("submitDstv", true);

        return "transactions/pay-bills";
    }

    //PROCESS DSTV PAYMENT
    @PostMapping("/dstv")
    @ResponseBody
    public String processDstv(Model model, TransactionForms dstvFormPin, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("dstvFormPin", dstvFormPin);
        String biller = "DSTV";
        payBillsService.cableTvPayment(session, biller);
        CableTvPaymentResponse cableTvPaymentResponse = (CableTvPaymentResponse) session.getAttribute("cableTvPaymentResponse");
        if (cableTvPaymentResponse.getResponseCode().equals("00")) {
            return "00";
        } else {
//            model.addAttribute("submitted", true);
            String customErrorMessage = cableTvPaymentResponse.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return cableTvPaymentResponse.getResponseMessage();
        }

    }

    //DSTV NAME ENQUIRY USING AJAX
    @GetMapping({"/validate/dstv/{cardNumber}"})
    @ResponseBody
    public String validateDstv(@PathVariable("cardNumber") String cardNumber, Model model, HttpSession session) throws UnirestException {
        String biller = "DSTV";
        ValidateCableTvResponsePayload validateCableTv = payBillsService.validateCableTv(session, biller, cardNumber);
        if (validateCableTv.getResponseCode().equals("00")) {
            log.info("NAME ENQUIRY DSTV {}", validateCableTv);
            String name = validateCableTv.getCardholderName();
            model.addAttribute("name", name);
            return name;
        } else {

            return "invalid";
        }

    }


    @PostMapping("/electricity-form")
    public String processElectricityForm(@ModelAttribute("electricityForm") TransactionForms electricityForm, Model model, HttpSession session) throws UnirestException {
        session.setAttribute("electricityForm", electricityForm);

        GetCableTvBillersResponsePayload gotvBillerPlan = (GetCableTvBillersResponsePayload) session.getAttribute("cableTvBillersResponse");
        GetCableTvBillersResponsePayload dstvBillerPlan = (GetCableTvBillersResponsePayload) session.getAttribute("cableTvBillersResponse");
        GetElectricityBillerResponsePayload electricityBiller = (GetElectricityBillerResponsePayload) session.getAttribute("electricityBillersResponse");

        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("gotvBillerPlan", gotvBillerPlan.getBillers());
        model.addAttribute("dstvBillerPlan", dstvBillerPlan.getBillers());
        model.addAttribute("electricityBillers", electricityBiller.getBiller());

        model.addAttribute("gotvForm", new TransactionForms());
        model.addAttribute("gotvFormPin", new TransactionForms());
        model.addAttribute("dstvForm", new TransactionForms());
        model.addAttribute("dstvFormPin", new TransactionForms());
        model.addAttribute("electricityForm", new TransactionForms());
        model.addAttribute("electricityFormPin", new TransactionForms());

        TransactionForms electricityTxnData = (TransactionForms) session.getAttribute("electricityForm");
        String bouquet = "";
        if (electricityTxnData != null) {
            String[] bouquetSplitted = electricityTxnData.getElectricityBillerSelect().split(",");
            bouquet = bouquetSplitted[1];
        }
        model.addAttribute("package", bouquet);
        model.addAttribute("electricityTxnData", electricityTxnData);

        model.addAttribute("submittedElectricity", true);

        return "transactions/pay-bills";
    }

    @PostMapping("/electricity")
    @ResponseBody
    public String processElectricity(Model model, TransactionForms electricityFormPin, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("electricityFormPin", electricityFormPin);
        payBillsService.electricityPayment(session);
        final GeneralResponsePayload electricityPaymentResponse = (GeneralResponsePayload) session.getAttribute("electricityPaymentResponse");
        if (electricityPaymentResponse.getResponseCode().equals("00")) {
            return "00";
        } else {
//            model.addAttribute("submitted", true);
            String customErrorMessage = electricityPaymentResponse.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return electricityPaymentResponse.getResponseMessage();
        }

    }

    //NAME ENQUIRY ELECTRICITY
    @GetMapping({"/validate/electricity/{meterNumber}/{biller}"})
    @ResponseBody
    public String validateElectricity(@PathVariable("meterNumber") String meterNumber, @PathVariable("biller") String biller, Model model, HttpSession session) throws UnirestException {

        ValidateElectricityResponsePayload validateElectricity = payBillsService.validateElectricity(session, biller, meterNumber);
        if (validateElectricity.getResponseCode().equals("00")) {
            log.info("NAME ENQUIRY ELECTRICITY {}", validateElectricity);
            String name = validateElectricity.getCardHolderName();
            model.addAttribute("name", name);
            return name;
        } else {
            return "invalid";
        }

    }


    //ELECTRICITY BILLERS LIST
    @GetMapping({"/electricity/{biller}"})
    @ResponseBody
    public String electricityBillers(@PathVariable("biller") String biller, HttpSession session) throws UnirestException {
        Gson gson = new Gson();
        GetElectricityBillerResponsePayload electricityBiller = payBillsService.getElectricityBillers(session, biller);
//        electricityBiller.getDataPlans().isEmpty() ||
        if (!electricityBiller.getResponseCode().equals("00")) {
            return gson.toJson(electricityBiller.getErrorMessage());
        }
        return gson.toJson(electricityBiller);
    }
}
