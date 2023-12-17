package com.iBanking.iBanking.controller;


import com.iBanking.iBanking.Forms.TransactionForms;
import com.iBanking.iBanking.payload.accout.AccountDetailsList;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponse;
import com.iBanking.iBanking.payload.customer.LoginResponsePayload;
import com.iBanking.iBanking.services.*;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Slf4j
@Controller
public class LoginController {

    @Autowired
    LoginService loginService;
    @Autowired
    CustomerService customerService;
    @Autowired
    AccountService accountService;
    @Autowired
    SendMoneyService sendMoneyService;
    @Autowired
    PayBillsService payBillsService;

    //Load the Login Page
    @GetMapping("/inet")
    public String showLogin(@RequestParam(value = "timeout", required = false) boolean timeout, Model model, HttpSession session) {
        model.addAttribute("loginForm", new TransactionForms());
//        session.invalidate();
        model.addAttribute("timeout", timeout);
        return "login";
    }

    //Process the Login Page, call the customer and account details as well
    @PostMapping("/inet")
    public String processLogin(@ModelAttribute TransactionForms loginForm, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException, ExecutionException, InterruptedException {
        session.setAttribute("loginForm", loginForm);
        try {
            loginService.login(session);
            LoginResponsePayload login = (LoginResponsePayload) session.getAttribute("loginResponse");
            if (login.getResponseCode().equals("00")) {
                session.setAttribute("loggedIn", true);
                CompletableFuture<CustomerDetailsResponse> customerDetailsFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        TransactionForms loginFormMobileNumber = (TransactionForms) session.getAttribute("loginForm");
                        return customerService.getCustomerDetails(session, loginFormMobileNumber.getMobileNumber());
                    } catch (UnirestException e) {
                        throw new RuntimeException("Error while getting customer details", e);
                    }
                });
                CompletableFuture<AccountDetailsList> accountBalanceFuture = CompletableFuture.supplyAsync(() -> {

                    try {
                        return accountService.getAccountBalances(session);
                    } catch (UnirestException e) {
                        throw new RuntimeException(e);
                    }
                });
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return sendMoneyService.getBankList(session);
                    } catch (UnirestException e) {
                        throw new RuntimeException("Error while getting Bank List", e);
                    }
                });
                CompletableFuture.supplyAsync(() -> {
                    try {
                        String gotvBiller = "GOTV";
                        return payBillsService.getCableTvBillers(session, gotvBiller);
                    } catch (UnirestException e) {
                        throw new RuntimeException("Error while getting customer details", e);
                    }
                });
                CompletableFuture.supplyAsync(() -> {
                    try {
                        String dstvBiller = "DSTV";

                        return payBillsService.getCableTvBillers(session, dstvBiller);
                    } catch (UnirestException e) {
                        throw new RuntimeException("Error while getting DSTV Biller", e);
                    }
                });
                CompletableFuture.supplyAsync(() -> {
                    try {
                        String biller = "E02E";
                        return payBillsService.getElectricityBillers(session, biller);
                    } catch (UnirestException e) {
                        throw new RuntimeException("Error while getting Electricity", e);
                    }
                });
                try {
                    accountBalanceFuture.get();
                    customerDetailsFuture.get();

                } catch (Exception e) {
                    log.error(e.getLocalizedMessage());
                    throw new RuntimeException("Error while waiting for async service calls", e);
                }

                return "redirect:/dashboard";
            } else {
                String customErrorMessage = login.getResponseMessage();
                redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
                return "redirect:/login";
            }
        } catch (Exception e) {
            log.info("Error while processing login {}", e.getMessage());
            throw new RuntimeException("Error while processing login", e);
        }

    }

    @Value("${session.timeout}")
    private String sessionTimeOut;

    @GetMapping("/properties")
    @ResponseBody
    public String getAppProperties() {
        return sessionTimeOut;
    }

}
