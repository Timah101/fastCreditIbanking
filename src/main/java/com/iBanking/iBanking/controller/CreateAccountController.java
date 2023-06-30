package com.iBanking.iBanking.controller;


import com.iBanking.iBanking.Forms.Forms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class CreateAccountController {

    @GetMapping("/open-account")
    public String showCreateAccount1(Model model) {
        model.addAttribute("createAccountForm1", new Forms());
        return "create-account/create-account-1";
    }

    @PostMapping("/open-account")
    public String processCreateAccount1(Model model, Forms createAccountForm1, HttpSession session) {
        session.setAttribute("createAccountForm1", createAccountForm1);

        return "redirect:/open-account-2";
    }

    @GetMapping("/open-account-2")
    public String showCreateAccount2(Model model) {
        model.addAttribute("createAccountForm2", new Forms());
        return "create-account/create-account-2";
    }

    @PostMapping("/open-account-2")
    public String processCreateAccount2(Model model, Forms createAccountForm2, HttpSession session) {
        session.setAttribute("createAccountForm2", createAccountForm2);

        return "redirect:/open-account-3";
    }

    @GetMapping("/open-account-3")
    public String showCreateAccount3(Model model) {
        model.addAttribute("createAccountForm3", new Forms());
        return "create-account/create-account-3";
    }

    @PostMapping("/open-account-3")
    public String processCreateAccount3(Model model, Forms createAccountForm3, HttpSession session) {
        session.setAttribute("createAccountForm3", createAccountForm3);

        return "redirect:/open-account-4";
    }

    @GetMapping("/open-account-4")
    public String showCreateAccount4(Model model) {
        model.addAttribute("createAccountForm4", new Forms());
        return "create-account/create-account-4";
    }

    @PostMapping("/open-account-4")
    public String processCreateAccount4(Model model, Forms createAccountForm4, HttpSession session) {
        session.setAttribute("createAccountForm4", createAccountForm4);

        return "redirect:/login";
    }
}
