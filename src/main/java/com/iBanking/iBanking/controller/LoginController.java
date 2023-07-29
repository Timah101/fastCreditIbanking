package com.iBanking.iBanking.controller;


import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.accout.AccountDetailsListResponsePayload;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponsePayload;
import com.iBanking.iBanking.payload.customer.LoginResponsePayload;
import com.iBanking.iBanking.services.AccountService;
import com.iBanking.iBanking.services.CustomerService;
import com.iBanking.iBanking.services.LoginService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    //Load the Login Page
    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "timeout", required = false) boolean timeout, Model model) {
        model.addAttribute("loginForm", new Forms());
        model.addAttribute("timeout", timeout);
        return "login";
    }

    //Process the Login Page, call the customer and account details as well
    @PostMapping("/login")
    public String processLogin(@ModelAttribute Forms loginForm, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException, ExecutionException, InterruptedException {
        session.setAttribute("loginForm", loginForm);
        try {
            LoginResponsePayload login = loginService.login(session);
            if (login.getResponseCode().equals("00")) {
                session.setAttribute("loggedIn", true);

                CompletableFuture<CustomerDetailsResponsePayload> customerDetailsFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        Forms loginFormMobileNumber = (Forms) session.getAttribute("loginForm");
                        return customerService.getCustomerDetails(session, loginFormMobileNumber.getMobileNumber());
                    } catch (UnirestException e) {
                        throw new RuntimeException("Error while getting customer details", e);
                    }
                });
//                CompletableFuture<AccountDetailsResponsePayload> accountDetailsFuture = CompletableFuture.supplyAsync(() -> {
//
//                    try {
//                        return accountService.getAccountDetails(session);
//                    } catch (UnirestException e) {
//                        throw new RuntimeException(e);
//                    }
//                });
                CompletableFuture<AccountDetailsListResponsePayload> accountBalanceFuture = CompletableFuture.supplyAsync(() -> {

                    try {
                        return accountService.getAccountBalances(session);
                    } catch (UnirestException e) {
                        throw new RuntimeException(e);
                    }
                });
                try {
                    AccountDetailsListResponsePayload accountBalance = accountBalanceFuture.get();
                    CustomerDetailsResponsePayload customerDetails = customerDetailsFuture.get();
//                    AccountDetailsResponsePayload accountDetails = accountDetailsFuture.get();
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


}
