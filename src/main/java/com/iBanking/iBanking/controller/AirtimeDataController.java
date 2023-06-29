package com.iBanking.iBanking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.accout.AccountDetailsListResponsePayload;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponsePayload;
import com.iBanking.iBanking.payload.generics.ResponseCodeResponseMessageResponsePayload;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataPlansResponsePayload;
import com.iBanking.iBanking.services.AccountService;
import com.iBanking.iBanking.services.AirtimeDataService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;
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

//        AccountDetailsListResponsePayload accountBalanceResponse = accountService.getAccountBalances(session);
        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");

        model.addAttribute("airtimeForm", new Forms());
        model.addAttribute("dataForm", new Forms());
        model.addAttribute("accountBalanceResponse", new AccountDetailsListResponsePayload());
        model.addAttribute("customerDetails", new CustomerDetailsResponsePayload());
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());

        return "transactions/airtime-data";
    }

    //PROCESS FIRST FORM SUBMIT AND REDIRECT TO CONFIRM PIN PAGE
    @PostMapping("/airtime-form")
    public String processAirtimeFormSubmit(@ModelAttribute("airtimeForm") Forms airtimeForm, HttpSession session, Model model) {
        session.setAttribute("airtimeForm", airtimeForm);
        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("airtimeForm", new Forms());
        model.addAttribute("airtimeFormPin", new Forms());
        model.addAttribute("dataFormPin", new Forms());
        model.addAttribute("airtimeFormPin", new Forms());
        model.addAttribute("dataForm", new Forms());
        model.addAttribute("dataFormPin", new Forms());
        model.addAttribute("accountBalanceResponse", new AccountDetailsListResponsePayload());
        model.addAttribute("customerDetails", new CustomerDetailsResponsePayload());
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());

        model.addAttribute("submitted", true);

        return "transactions/airtime-data";
    }

    @PostMapping("/airtime")
    public String processAirtime(@ModelAttribute Forms airtimeFormPin, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {

        session.setAttribute("airtimeFormPin", airtimeFormPin);
        airtimeService.airtimeTopUp(session);
        ResponseCodeResponseMessageResponsePayload airtimeTopUp = (ResponseCodeResponseMessageResponsePayload) session.getAttribute("airtimeTopUpResponse");
        if (airtimeTopUp.getResponseCode().equals("00")) {
            return "redirect:/airtime-data";
        } else {
            String customErrorMessage = airtimeTopUp.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/airtime-data";
        }

    }


    //PROCESS FIRST FORM SUBMIT AND REDIRECT TO CONFIRM PIN PAGE
    @PostMapping("/data-form")
    public String processDataFormSubmit(@ModelAttribute("dataForm") Forms dataForm, HttpSession session, Model model) throws UnirestException {
        session.setAttribute("dataForm", dataForm);
        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("airtimeForm", new Forms());
        model.addAttribute("dataFormPin", new Forms());
        model.addAttribute("airtimeFormPin", new Forms());
        model.addAttribute("dataForm", new Forms());
        model.addAttribute("accountBalanceResponse", new AccountDetailsListResponsePayload());
        model.addAttribute("customerDetails", new CustomerDetailsResponsePayload());
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());

        model.addAttribute("submitData", true);

        return "transactions/airtime-data";
    }


    @PostMapping("/data")
    public String processData(@ModelAttribute Forms dataFormPin, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {

        session.setAttribute("dataFormPin", dataFormPin);
        airtimeService.dataTopUp(session);
        ResponseCodeResponseMessageResponsePayload dataTopUp = (ResponseCodeResponseMessageResponsePayload) session.getAttribute("dataTopUpResponse");
        if (dataTopUp.getResponseCode().equals("00")) {
            return "redirect:/airtime-data";
        } else {
            String customErrorMessage = dataTopUp.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/airtime-data";
        }

    }

    @GetMapping({"/data/{telco}"})
    @ResponseBody
    public String dataPlansEnquiry(@PathVariable("telco") String telco, HttpSession session) throws IOException, UnirestException {
        Gson gson = new Gson();
        DataPlansResponsePayload dataPlan = airtimeService.dataPlans(session, telco);

        if (dataPlan.getDataPlans().isEmpty() || !dataPlan.getResponseCode().equals("00")) {
            return "error fetching data";
        }
        return gson.toJson(dataPlan);
    }
}
