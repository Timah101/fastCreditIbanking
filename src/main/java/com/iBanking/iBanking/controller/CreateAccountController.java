package com.iBanking.iBanking.controller;


import com.iBanking.iBanking.Forms.Forms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class CreateAccountController {

    @GetMapping("/open/account/1")
    public String showCreateAccount1(Model model) {
        model.addAttribute("createAccountForm", new Forms());
        return "create-account/create-account-1";
    }

    @GetMapping("/open/account/2")
    public String showCreateAccount2(Model model) {
        model.addAttribute("createAccountForm", new Forms());
        return "create-account/create-account-2";
    }
}
