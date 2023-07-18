package com.iBanking.iBanking.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class CustomErrorControllerAdvice implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @RequestMapping(ERROR_PATH)
    public String handleError(HttpServletRequest request, Model model, HttpSession session) {
        
        boolean loggedIn = (boolean) session.getAttribute("loggedInError");

        // Add the loggedIn attribute to the model
        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("errorMessage", "An unexpected error occurred");
//        return "error-page"; // Return the name of your custom error page template
        return "redirect:/login";
    }
}



