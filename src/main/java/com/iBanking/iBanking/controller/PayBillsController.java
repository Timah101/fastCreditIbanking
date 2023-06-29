package com.iBanking.iBanking.controller;

import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.accout.AccountDetailsListResponsePayload;
import com.iBanking.iBanking.payload.transactions.airtimeData.DataPlansResponsePayload;
import com.iBanking.iBanking.payload.transactions.cableTv.CableTvPaymentResponse;
import com.iBanking.iBanking.payload.transactions.cableTv.GetCableTvBillersResponsePayload;
import com.iBanking.iBanking.payload.transactions.cableTv.ValidateCableTvResponsePayload;
import com.iBanking.iBanking.payload.transactions.sendMoney.OtherBanksNameEnquiryResponsePayload;
import com.iBanking.iBanking.services.PayBillsService;
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
public class PayBillsController {

    @Autowired
    PayBillsService payBillsService;

    @GetMapping("/pay-bills")
    public String showPayBills(Model model, HttpSession session) throws UnirestException {


        String gotvBiller = "GOTV";
        String dstvBiller = "DSTV";
        GetCableTvBillersResponsePayload gotvBillerPlan = payBillsService.getCableTvBillers(session, gotvBiller);
        GetCableTvBillersResponsePayload dstvBillerPlan = payBillsService.getCableTvBillers(session, dstvBiller);
        String selectedOptionGotv = "";
        String selectedOptionDstv = "";
        model.addAttribute("selectedOptionGotv", selectedOptionGotv);
        model.addAttribute("selectedOptionDstv", selectedOptionDstv);

        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("gotvBillerPlan", gotvBillerPlan.getBillers());
        model.addAttribute("dstvBillerPlan", dstvBillerPlan.getBillers());
        model.addAttribute("gotvForm", new Forms());
        model.addAttribute("gotvFormPin", new Forms());
        model.addAttribute("dstvForm", new Forms());
        model.addAttribute("dstvFormPin", new Forms());

        return "transactions/pay-bills";
    }

    @PostMapping("/gotv-form")
    public String processGotvForm(@ModelAttribute("gotvForm") Forms gotvForm, Model model, HttpSession session) throws UnirestException {
        session.setAttribute("gotvForm", gotvForm);
        String gotvBiller = "GOTV";
        String dstvBiller = "DSTV";
        GetCableTvBillersResponsePayload gotvBillerPlan = payBillsService.getCableTvBillers(session, gotvBiller);
        GetCableTvBillersResponsePayload dstvBillerPlan = payBillsService.getCableTvBillers(session, dstvBiller);

        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("gotvBillerPlan", gotvBillerPlan.getBillers());
        model.addAttribute("dstvBillerPlan", dstvBillerPlan.getBillers());

        model.addAttribute("gotvForm", new Forms());
        model.addAttribute("gotvFormPin", new Forms());
        model.addAttribute("dstvForm", new Forms());
        model.addAttribute("dstvFormPin", new Forms());

        model.addAttribute("submitted", true);

        return "transactions/pay-bills";
    }

    @PostMapping("/gotv")
    public String processGotv(Model model, Forms gotvFormPin, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("gotvFormPin", gotvFormPin);

        payBillsService.cableTvPayment(session);
        CableTvPaymentResponse cableTvPaymentResponse = (CableTvPaymentResponse) session.getAttribute("cableTvPaymentResponse");
        if (cableTvPaymentResponse.getResponseCode().equals("00")) {
            return "redirect:/pay-bills";
        } else {
            model.addAttribute("submitted", true);
            String customErrorMessage = cableTvPaymentResponse.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/pay-bills";
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
            String name = validateCableTv.getCardHolderName();
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
    public String processDstvForm(@ModelAttribute("dstvForm") Forms gotvForm, Model model, HttpSession session) throws UnirestException {
        session.setAttribute("dstvForm", gotvForm);

        String gotvBiller = "GOTV";
        String dstvBiller = "DSTV";
        GetCableTvBillersResponsePayload gotvBillerPlan = payBillsService.getCableTvBillers(session, gotvBiller);
        GetCableTvBillersResponsePayload dstvBillerPlan = payBillsService.getCableTvBillers(session, dstvBiller);
        AccountDetailsListResponsePayload accountBalanceResponse = (AccountDetailsListResponsePayload) session.getAttribute("accountBalanceResponse");
        model.addAttribute("accountBalanceResponse", accountBalanceResponse.getAccountList());
        model.addAttribute("gotvBillerPlan", gotvBillerPlan.getBillers());
        model.addAttribute("dstvBillerPlan", dstvBillerPlan.getBillers());
        model.addAttribute("gotvForm", new Forms());
        model.addAttribute("gotvFormPin", new Forms());
        model.addAttribute("dstvForm", new Forms());
        model.addAttribute("dstvFormPin", new Forms());

        model.addAttribute("submitDstv", true);

        return "transactions/pay-bills";
    }

    //PROCESS DSTV PAYMENT
    @PostMapping("/dstv")
    public String processDstv(Model model, Forms dstvFormPin, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("dstvFormPin", dstvFormPin);
        payBillsService.cableTvPayment(session);
        CableTvPaymentResponse cableTvPaymentResponse = (CableTvPaymentResponse) session.getAttribute("cableTvPaymentResponse");
        if (cableTvPaymentResponse.getResponseCode().equals("00")) {
            return "redirect:/pay-bills";
        } else {
            model.addAttribute("submitted", true);
            String customErrorMessage = cableTvPaymentResponse.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/pay-bills";
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
            String name = validateCableTv.getCardHolderName();
            model.addAttribute("name", name);
            return name;
        } else {

            return "invalid";
        }

    }

}
