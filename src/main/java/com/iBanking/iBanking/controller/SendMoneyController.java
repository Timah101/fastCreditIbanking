package com.iBanking.iBanking.controller;

import com.iBanking.iBanking.Forms.SendMoneyForms;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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
        model.addAttribute("sendMoneyLocalForm", new SendMoneyForms());
        model.addAttribute("sendMoneyLocalFormPin", new SendMoneyForms());
        model.addAttribute("sendMoneyOthersPin", new SendMoneyForms());
        model.addAttribute("sendMoneyOthersForm", new SendMoneyForms());
        model.addAttribute("mobileView", true);
        model.addAttribute("showLocal", true);
        return "transactions/send-money";
    }

    //Reload to Confirm OTP div on same page
    @PostMapping("/local-form")
    public String processSendMoneyLocalFormSubmit(@Valid @ModelAttribute("sendMoneyLocalForm") SendMoneyForms sendMoneyLocalForm,  BindingResult result,
                                                  HttpSession session, Model model) throws UnirestException {
        session.setAttribute("sendMoneyLocalForm", sendMoneyLocalForm);
        if (result.hasErrors()) {
            AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
            sendMoneyService.getBankList(session);
            GetBankListPResponsePayload bankList = (GetBankListPResponsePayload) session.getAttribute("getBankListResponse");
            model.addAttribute("bankListResponse", bankList.getBankList());
            model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
            model.addAttribute("sendMoneyLocalFormPin", new SendMoneyForms());
            model.addAttribute("sendMoneyOthersPin", new SendMoneyForms());
            model.addAttribute("sendMoneyOthersForm", new SendMoneyForms());
            model.addAttribute("mobileView", true);
            model.addAttribute("showLocal", true);
            return "transactions/send-money";
        }
        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("sendMoneyLocalForm", new SendMoneyForms());
        model.addAttribute("sendMoneyLocalFormPin", new SendMoneyForms());
        model.addAttribute("sendMoneyOthersPin", new SendMoneyForms());
        model.addAttribute("sendMoneyOthersForm", new SendMoneyForms());

        SendMoneyForms localTxnData = (SendMoneyForms) session.getAttribute("sendMoneyLocalForm");
        model.addAttribute("localTxnData", localTxnData);
        model.addAttribute("submitted", true);
        model.addAttribute("mobileView", false);

        return "transactions/send-money";
    }

    //Process Send Money and return error if any
    @PostMapping("/send-money/local")
    @ResponseBody
    public String processSendMoneyLocal(@ModelAttribute SendMoneyForms sendMoneyLocalFormPin,
                                        HttpSession session, RedirectAttributes redirectAttributes, Model model) throws UnirestException {
        session.setAttribute("sendMoneyLocalFormPin", sendMoneyLocalFormPin);
        sendMoneyService.sendMoneyLocal(session);

        GeneralResponsePayload sendMoneyLocal = (GeneralResponsePayload) session.getAttribute("sendMoneyLocalResponse");
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
    public String processSendMoneyOthersFormSubmit(@Valid @ModelAttribute("sendMoneyOthersForm") SendMoneyForms sendMoneyOthersForm, BindingResult result,
                                                   HttpSession session, Model model) throws UnirestException {
        session.setAttribute("sendMoneyOthersForm", sendMoneyOthersForm);
        if (result.hasErrors()) {
            AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
            sendMoneyService.getBankList(session);
            GetBankListPResponsePayload bankList = (GetBankListPResponsePayload) session.getAttribute("getBankListResponse");
            model.addAttribute("bankListResponse", bankList.getBankList());
            model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
            model.addAttribute("sendMoneyLocalFormPin", new SendMoneyForms());
            model.addAttribute("sendMoneyOthersPin", new SendMoneyForms());
            model.addAttribute("sendMoneyLocalForm", new SendMoneyForms());
            model.addAttribute("mobileView", true);
            model.addAttribute("showOthers", true);
            return "transactions/send-money";
        }
        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("sendMoneyLocalForm", new SendMoneyForms());
        model.addAttribute("sendMoneyLocalFormPin", new SendMoneyForms());
        model.addAttribute("sendMoneyOthersPin", new SendMoneyForms());
        model.addAttribute("sendMoneyOthersForm", new SendMoneyForms());

        SendMoneyForms othersTxnData = (SendMoneyForms) session.getAttribute("sendMoneyOthersForm");
        model.addAttribute("othersTxnData", othersTxnData);
        model.addAttribute("submittedOthers", true);
        model.addAttribute("mobileView", false);

        return "transactions/send-money";
    }

    //Process Send Money to Other Banks and return errors if any
    @PostMapping("/send-money/others")
    @ResponseBody
    public String processSendMoneyOthers(@ModelAttribute SendMoneyForms sendMoneyOthersFormPin, HttpSession session, RedirectAttributes redirectAttributes, Model model) throws UnirestException {
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

    @PostMapping("/toggle-local")
    public String toggleLocal(Model model, HttpSession session) throws UnirestException {
        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        sendMoneyService.getBankList(session);
        GetBankListPResponsePayload bankList = (GetBankListPResponsePayload) session.getAttribute("getBankListResponse");
        model.addAttribute("bankListResponse", bankList.getBankList());
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("sendMoneyLocalForm", new SendMoneyForms());
        model.addAttribute("sendMoneyLocalFormPin", new SendMoneyForms());
        model.addAttribute("sendMoneyOthersPin", new SendMoneyForms());
        model.addAttribute("sendMoneyOthersForm", new SendMoneyForms());
        model.addAttribute("mobileView", true);
        model.addAttribute("showLocal", true);
        return "transactions/send-money";
    }

    @PostMapping("/toggle-others")
    public String toggleOthers(Model model, HttpSession session) throws UnirestException {
        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        sendMoneyService.getBankList(session);
        GetBankListPResponsePayload bankList = (GetBankListPResponsePayload) session.getAttribute("getBankListResponse");
        model.addAttribute("bankListResponse", bankList.getBankList());
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("sendMoneyLocalForm", new SendMoneyForms());
        model.addAttribute("sendMoneyLocalFormPin", new SendMoneyForms());
        model.addAttribute("sendMoneyOthersPin", new SendMoneyForms());
        model.addAttribute("sendMoneyOthersForm", new SendMoneyForms());
        model.addAttribute("mobileView", true);
        model.addAttribute("showOthers", true);
        return "transactions/send-money";
    }
}
