package com.iBanking.iBanking.controller;

import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.accout.AccountDetailsListResponsePayload;
import com.iBanking.iBanking.payload.accout.AccountDetailsResponsePayload;
import com.iBanking.iBanking.payload.generics.ResponseCodeResponseMessageResponsePayload;
import com.iBanking.iBanking.services.AccountService;
import com.iBanking.iBanking.services.SendMoneyLocalService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Controller
@Slf4j
public class SendMoneyController {
    @Autowired
    SendMoneyLocalService sendMoneyLocalService;
    @Autowired
    AccountService accountService;

    @GetMapping("/send-money/local")
    public String sendMoneyLocal(Model model, HttpSession session) {

        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("sendMoneyLocalForm", new Forms());
        return "send-money-local";
    }

    @PostMapping("/send-money/local")
    public String processSendMoneyLocal(@ModelAttribute Forms sendMoneyLocalForm, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("sendMoneyLocalForm", sendMoneyLocalForm);
        ResponseCodeResponseMessageResponsePayload sendMoneyLocal = sendMoneyLocalService.sendMoneyLocal(session);
        String customErrorMessage = sendMoneyLocal.getResponseMessage();
        redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
        return "redirect:/send-money/local";
    }

    @GetMapping({"/name/lookup/local/{accountNumber}"})
    @ResponseBody
    public String localNameEnquiry(@PathVariable("accountNumber") String accountNumber, Model model, HttpSession session) throws UnirestException {
        AccountDetailsResponsePayload accountDetailsLocal = accountService.getAccountDetailsLocal(session, accountNumber);
        if (accountDetailsLocal.getResponseCode().equals("00")) {
            log.info("NAME ENQUIRY {}", accountDetailsLocal);
            String name = accountDetailsLocal.getAccountName();
            model.addAttribute("name", name);
            return name;
        } else {

            return "invalid";
        }

    }
}
