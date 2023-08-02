package com.iBanking.iBanking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.AirtimeDataTransactionForms;
import com.iBanking.iBanking.Forms.DataTransactionForms;
import com.iBanking.iBanking.Forms.PinForm;
import com.iBanking.iBanking.Forms.TransactionForms;
import com.iBanking.iBanking.payload.accout.AccountDetailsListResponsePayload;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponsePayload;
import com.iBanking.iBanking.payload.generics.GeneralResponsePayload;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataPlansResponsePayload;
import com.iBanking.iBanking.services.AccountService;
import com.iBanking.iBanking.services.AirtimeDataService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@Slf4j
public class AirtimeDataController {

    @Autowired
    AirtimeDataService airtimeService;
    @Autowired
    AccountService accountService;

    @GetMapping("/airtime-data")
    public String showAirtimeData(Model model, HttpSession session) throws UnirestException, JsonProcessingException {

        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");

        model.addAttribute("airtimeForm", new AirtimeDataTransactionForms());
        model.addAttribute("dataForm", new DataTransactionForms());
        model.addAttribute("accountBalanceResponse", new AccountDetailsListResponsePayload());
        model.addAttribute("customerDetails", new CustomerDetailsResponsePayload());
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        String selectedOption = "";
        model.addAttribute("selectedOptionGotv", selectedOption);
        model.addAttribute("mobileView", true);
        model.addAttribute("showAirtime", true);
//        model.addAttribute("showData", true);

        return "transactions/airtime-data";
    }

    //PROCESS FIRST FORM SUBMIT AND REDIRECT TO CONFIRM PIN PAGE
    @PostMapping("/airtime-form")
    public String processAirtimeFormSubmit(@Valid @ModelAttribute("airtimeForm") AirtimeDataTransactionForms airtimeForm, BindingResult result, HttpSession session, Model model) {
        session.setAttribute("airtimeForm", airtimeForm);
        if (result.hasErrors()) {
            System.out.println("TRIED TO VALIDATE THE NUMBER HERE ");
            System.out.println(result.getAllErrors());
            AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");

            model.addAttribute("dataForm", new DataTransactionForms());
            model.addAttribute("accountBalanceResponse", new AccountDetailsListResponsePayload());
            model.addAttribute("customerDetails", new CustomerDetailsResponsePayload());
            model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
            String selectedOption = "";
            model.addAttribute("selectedOptionGotv", selectedOption);
            model.addAttribute("mobileView", true);
            model.addAttribute("showAirtime", true);
            return "transactions/airtime-data";
        }
        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");

        model.addAttribute("dataForm", new DataTransactionForms());
        model.addAttribute("airtimeFormPin", new PinForm());
        model.addAttribute("dataFormPin", new PinForm());
        model.addAttribute("accountBalanceResponse", new AccountDetailsListResponsePayload());
        model.addAttribute("customerDetails", new CustomerDetailsResponsePayload());
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());

        AirtimeDataTransactionForms airtimeTxnData = (AirtimeDataTransactionForms) session.getAttribute("airtimeForm");
        model.addAttribute("airtimeTxnData", airtimeTxnData);

        model.addAttribute("submitted", true);
        model.addAttribute("mobileView", false);
        model.addAttribute("showAirtime", false);

        return "transactions/airtime-data";
    }

    @PostMapping("/airtime")
    @ResponseBody
    public String processAirtime(@Valid @ModelAttribute PinForm airtimeFormPin, BindingResult result, HttpSession session,
                                 RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("airtimeFormPin", airtimeFormPin);
        airtimeService.airtimeTopUp(session);
        GeneralResponsePayload airtimeTopUp = (GeneralResponsePayload) session.getAttribute("airtimeTopUpResponse");
        if (airtimeTopUp.getResponseCode().equals("00")) {
            return "00";
        } else {
            String customErrorMessage = airtimeTopUp.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return airtimeTopUp.getResponseMessage();
        }

    }

    //PROCESS FIRST FORM SUBMIT AND REDIRECT TO CONFIRM PIN PAGE
    @PostMapping("/data-form")
    public String processDataFormSubmit(@Valid @ModelAttribute("dataForm") DataTransactionForms dataForm, BindingResult result, HttpSession session, Model model) throws UnirestException {
        session.setAttribute("dataForm", dataForm);
        if (result.hasErrors()) {
            System.out.println("TRIED TO VALIDATE THE NUMBER HERE ");
            System.out.println(result.getAllErrors());
            AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
            model.addAttribute("showData", true);
            model.addAttribute("dataForm", new DataTransactionForms());
            model.addAttribute("accountBalanceResponse", new AccountDetailsListResponsePayload());
            model.addAttribute("customerDetails", new CustomerDetailsResponsePayload());
            model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
            String selectedOption = "";
            model.addAttribute("selectedOptionGotv", selectedOption);


            return "transactions/airtime-data";
        }
        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("airtimeFormPin", new PinForm());
        model.addAttribute("dataFormPin", new PinForm());
        model.addAttribute("accountBalanceResponse", new AccountDetailsListResponsePayload());
        model.addAttribute("customerDetails", new CustomerDetailsResponsePayload());
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());

        DataTransactionForms dataTxnData = (DataTransactionForms) session.getAttribute("dataForm");
        model.addAttribute("dataTxnData", dataTxnData);
        model.addAttribute("submitData", true);
        model.addAttribute("mobileView", false);

        return "transactions/airtime-data";
    }


    @PostMapping("/data")
    @ResponseBody
    public String processData(@ModelAttribute PinForm dataFormPin, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("dataFormPin", dataFormPin);
        airtimeService.dataTopUp(session);
        GeneralResponsePayload dataTopUp = (GeneralResponsePayload) session.getAttribute("dataTopUpResponse");
        if (dataTopUp.getResponseCode().equals("00")) {
            return "00";
        } else {
            String customErrorMessage = dataTopUp.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return dataTopUp.getResponseMessage();
        }

    }

    @GetMapping({"/data2/{telco}"})
    @ResponseBody
    public String dataPlansEnquiry(@PathVariable("telco") String telco, HttpSession session) throws IOException, UnirestException {
        Gson gson = new Gson();
        DataPlansResponsePayload dataPlan = airtimeService.dataPlans(session, telco);

        if (dataPlan.getDataPlans().isEmpty() || !dataPlan.getResponseCode().equals("00")) {
            return "error fetching data";
        }
        return gson.toJson(dataPlan);
    }


    @PostMapping("/toggle-airtime")
    public String toggleAirtime(Model model, HttpSession session) {

        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");

        model.addAttribute("airtimeForm", new AirtimeDataTransactionForms());
        model.addAttribute("dataForm", new DataTransactionForms());
        model.addAttribute("accountBalanceResponse", new AccountDetailsListResponsePayload());
        model.addAttribute("customerDetails", new CustomerDetailsResponsePayload());
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        String selectedOption = "";
        model.addAttribute("selectedOptionGotv", selectedOption);
        model.addAttribute("mobileView", true);
        model.addAttribute("showAirtime", true);
//        model.addAttribute("showData", true);
        return "transactions/airtime-data";
    }

    @PostMapping("/toggle-data")
    public String toggleData(Model model, HttpSession session) {

        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");

        model.addAttribute("airtimeForm", new AirtimeDataTransactionForms());
        model.addAttribute("dataForm", new DataTransactionForms());
        model.addAttribute("accountBalanceResponse", new AccountDetailsListResponsePayload());
        model.addAttribute("customerDetails", new CustomerDetailsResponsePayload());
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        String selectedOption = "";
        model.addAttribute("selectedOptionGotv", selectedOption);
        model.addAttribute("mobileView", true);
//        model.addAttribute("showAirtime", true);
        model.addAttribute("showData", true);
        return "transactions/airtime-data";
    }
}
