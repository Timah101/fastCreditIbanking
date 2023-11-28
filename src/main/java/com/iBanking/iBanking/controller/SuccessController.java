package com.iBanking.iBanking.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class SuccessController {

    @GetMapping("/success")
    public String success(Model model) throws UnirestException {
        String URL = "https://www.jumia.com.ng/";
        model.addAttribute("url", URL);
        return "success";
    }

    @PostMapping("/success")
    public String submitForm() {

        return "redirect:/success";
    }
}
