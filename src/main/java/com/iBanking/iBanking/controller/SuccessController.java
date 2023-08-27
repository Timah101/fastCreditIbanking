package com.iBanking.iBanking.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class SuccessController {

    @GetMapping("/success")
    public String success(Model model, HttpSession session) throws UnirestException {

        return "success";
    }

    @PostMapping("/success")
    public String submitForm() {

        return "redirect:/success";
    }
}
