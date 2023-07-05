package com.iBanking.iBanking.controller;

import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.accout.AccountDetailsListResponsePayload;
import com.iBanking.iBanking.payload.accout.AccountDetailsResponsePayload;
import com.iBanking.iBanking.payload.generics.GeneralResponsePayload;
import com.iBanking.iBanking.payload.transactions.sendMoney.GetBankListPResponsePayload;
import com.iBanking.iBanking.payload.transactions.sendMoney.OtherBanksNameEnquiryResponsePayload;
import com.iBanking.iBanking.services.AccountService;
import com.iBanking.iBanking.services.SendMoneyService;
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
public class SendMoneyController {
    @Autowired
    SendMoneyService sendMoneyService;
    @Autowired
    AccountService accountService;

    //Loan Send Money Page
    @GetMapping("/send-money")
    public String sendMoneyLocal(Model model, HttpSession session) throws UnirestException {
        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        sendMoneyService.getBankList(session);
        GetBankListPResponsePayload bankList = (GetBankListPResponsePayload) session.getAttribute("getBankListResponse");
        model.addAttribute("bankListResponse", bankList.getBankList());
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("sendMoneyLocalForm", new Forms());
        model.addAttribute("sendMoneyLocalFormPin", new Forms());
        model.addAttribute("sendMoneyOthersPin", new Forms());
        model.addAttribute("sendMoneyOthersForm", new Forms());
        return "transactions/send-money";
    }

    //Reload to Confirm OTP div on same page
    @PostMapping("/local-form")
    public String processSendMoneyLocalFormSubmit(@ModelAttribute("sendMoneyLocalForm") Forms sendMoneyLocalForm, HttpSession session, Model model) throws UnirestException {
        session.setAttribute("sendMoneyLocalForm", sendMoneyLocalForm);

        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("sendMoneyLocalForm", new Forms());
        model.addAttribute("sendMoneyLocalFormPin", new Forms());
        model.addAttribute("sendMoneyOthersPin", new Forms());
        model.addAttribute("sendMoneyOthersForm", new Forms());

        Forms localTxnData = (Forms) session.getAttribute("sendMoneyLocalForm");
        model.addAttribute("localTxnData", localTxnData);
        model.addAttribute("submitted", true);

        return "transactions/send-money";
    }

    //Process Send Money and return error if any
    @PostMapping("/send-money/local")
    @ResponseBody
    public String processSendMoneyLocal(@ModelAttribute Forms sendMoneyLocalFormPin, HttpSession session, RedirectAttributes redirectAttributes, Model model) throws UnirestException {
        session.setAttribute("sendMoneyLocalFormPin", sendMoneyLocalFormPin);
        GeneralResponsePayload sendMoneyLocal = sendMoneyService.sendMoneyLocal(session);
        if (sendMoneyLocal.getResponseCode().equals("00")) {
            return "00";
        } else {
            model.addAttribute("submitted", true);
            String customErrorMessage = sendMoneyLocal.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return sendMoneyLocal.getResponseMessage();
        }
    }

    //Do Local Name Enquiry using Ajax
    @GetMapping({"/name/lookup/local/{accountNumber}"})
    @ResponseBody
    public String localNameEnquiry(@PathVariable("accountNumber") String accountNumber, Model model, HttpSession session) throws UnirestException {
        log.info("ARE YOU GETTING HERE FOR LOCAL {}", "YES");
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


    //Reload to Confirm OTP div on same page
    @PostMapping("/others-form")
    public String processSendMoneyOthersFormSubmit(@ModelAttribute("sendMoneyLocalForm") Forms sendMoneyLocalForm, HttpSession session, Model model) throws UnirestException {
        session.setAttribute("sendMoneyOthersForm", sendMoneyLocalForm);

        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("sendMoneyLocalForm", new Forms());
        model.addAttribute("sendMoneyLocalFormPin", new Forms());
        model.addAttribute("sendMoneyOthersPin", new Forms());
        model.addAttribute("sendMoneyOthersForm", new Forms());

        Forms othersTxnData = (Forms) session.getAttribute("sendMoneyOthersForm");
        model.addAttribute("othersTxnData", othersTxnData);
        model.addAttribute("submittedOthers", true);

        return "transactions/send-money";
    }

    //Process Send Money to Other Banks and return errors if any
    @PostMapping("/send-money/others")
    @ResponseBody
    public String processSendMoneyOthers(@ModelAttribute Forms sendMoneyOthersFormPin, HttpSession session, RedirectAttributes redirectAttributes, Model model) throws UnirestException {
        session.setAttribute("sendMoneyOthersFormPin", sendMoneyOthersFormPin);
        GeneralResponsePayload sendMoneyOthers = sendMoneyService.sendMoneyOthers(session);
        if (sendMoneyOthers.getResponseCode().equals("00")) {
            return "00";
        } else {
            model.addAttribute("submitted", true);
            String customErrorMessage = sendMoneyOthers.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return sendMoneyOthers.getResponseMessage();
        }
    }

    @GetMapping({"/name/lookup/others/{beneficiaryAccount}/{beneficiaryBankCode}"})
    @ResponseBody
    public String othersNameEnquiry(@PathVariable("beneficiaryAccount") String beneficiaryAccount, @PathVariable("beneficiaryBankCode") String beneficiaryBankCode, Model model, HttpSession session) throws UnirestException {

        OtherBanksNameEnquiryResponsePayload nameEnquiry = sendMoneyService.otherBanksNameEnquiry(session, beneficiaryAccount, beneficiaryBankCode);
        if (nameEnquiry.getResponseCode().equals("00")) {
            log.info("NAME ENQUIRY OTHERS {}", nameEnquiry);
            String name = nameEnquiry.getAccountName();
            model.addAttribute("name", name);
            return name;
        } else {

            return "invalid";
        }

    }
}
