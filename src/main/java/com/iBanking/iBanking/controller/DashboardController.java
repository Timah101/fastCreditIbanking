package com.iBanking.iBanking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.TransactionForms;
import com.iBanking.iBanking.payload.accout.AccountDetailsList;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponse;
import com.iBanking.iBanking.services.AccountService;
import com.iBanking.iBanking.services.AirtimeDataService;
import com.iBanking.iBanking.services.CustomerService;
import com.iBanking.iBanking.services.SendMoneyService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @Autowired
    CustomerService customerService;
    @Autowired
    AirtimeDataService airtimeService;
    @Autowired
    SendMoneyService sendMoneyService;

    @Autowired
    AccountService accountService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) throws UnirestException, JsonProcessingException {

        model.addAttribute("airtimeForm", new TransactionForms());
        model.addAttribute("sendMoneyLocalForm", new TransactionForms());

        CustomerDetailsResponse customerDetails = (CustomerDetailsResponse) session.getAttribute("customerDetailsResponse");
        AccountDetailsList accountBalanceResponse = accountService.getAccountBalances(session);


        if (!customerDetails.getResponseCode().equals("00") || !accountBalanceResponse.getResponseCode().equals("00")) {
            model.addAttribute("accountBalanceResponse", new AccountDetailsList());
            model.addAttribute("customerDetails", new CustomerDetailsResponse());
        }
        System.out.println("CUSTOMER DETAILS FOR SEND MONEY " + new Gson().toJson(customerDetails));

        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("customerDetails", customerDetails);

        return "dashboard";
    }
//    @GetMapping("/dashboard-main")
//    public String dashboardMain(Model model, HttpSession session) throws UnirestException, JsonProcessingException {
//
//        model.addAttribute("airtimeForm", new TransactionForms());
//        model.addAttribute("sendMoneyLocalForm", new TransactionForms());
//
//        CustomerDetailsResponse customerDetails = (CustomerDetailsResponse) session.getAttribute("customerDetailsResponse");
//        AccountDetailsList accountBalanceResponse = accountService.getAccountBalances(session);
//
//        if (!customerDetails.getResponseCode().equals("00") || !accountBalanceResponse.getResponseCode().equals("00")) {
//            model.addAttribute("accountBalanceResponse", new AccountDetailsList());
//            model.addAttribute("customerDetails", new CustomerDetailsResponse());
//        }
//
//        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
//        model.addAttribute("customerDetails", customerDetails);
//
//        return "dashboard-2";
//    }

    @PostMapping("/dashboard")
    public String processDashboard(HttpSession session) {

        return "/dashboard";
    }


}
