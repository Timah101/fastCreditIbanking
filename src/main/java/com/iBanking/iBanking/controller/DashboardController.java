package com.iBanking.iBanking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.accout.AccountDetailsListResponsePayload;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponsePayload;
import com.iBanking.iBanking.payload.generics.ResponseCodeResponseMessageResponsePayload;
import com.iBanking.iBanking.services.AccountService;
import com.iBanking.iBanking.services.AirtimeService;
import com.iBanking.iBanking.services.CustomerService;
import com.iBanking.iBanking.services.SendMoneyLocalService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @Autowired
    CustomerService customerService;
    @Autowired
    AirtimeService airtimeService;
    @Autowired
    SendMoneyLocalService sendMoneyLocalService;

    @Autowired
    AccountService accountService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) throws UnirestException, JsonProcessingException {

        model.addAttribute("airtimeForm", new Forms());
        model.addAttribute("sendMoneyLocalForm", new Forms());

        CustomerDetailsResponsePayload customerDetails = (CustomerDetailsResponsePayload) session.getAttribute("customerDetailsResponse");
        AccountDetailsListResponsePayload accountBalanceResponse = accountService.getAccountBalances(session);

        if (!customerDetails.getResponseCode().equals("00") || !accountBalanceResponse.getResponseCode().equals("00")) {
            model.addAttribute("accountBalanceResponse", new AccountDetailsListResponsePayload());
            model.addAttribute("customerDetails", new CustomerDetailsResponsePayload());
        }

        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("customerDetails", customerDetails);

        return "dashboard";
    }

    @PostMapping("/dashboard")
    public String processDashboard(HttpSession session) {


        return "/dashboard";
    }

    @PostMapping("/airtime")
    public String processAirtime(@ModelAttribute Forms airtimeForm, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {

        session.setAttribute("airtimeForm", airtimeForm);
        ResponseCodeResponseMessageResponsePayload airtimeTopUp = airtimeService.airtimeTopUp(session);
        String customErrorMessage = airtimeTopUp.getResponseMessage();
        redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
        return "redirect:/dashboard";
    }



}
