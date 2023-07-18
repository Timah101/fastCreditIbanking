package com.iBanking.iBanking.controller;

import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.SendOtpResponsePayload;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponsePayload;
import com.iBanking.iBanking.payload.generics.GeneralResponsePayload;
import com.iBanking.iBanking.services.CustomerService;
import com.iBanking.iBanking.services.SendOtpService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
public class ResetPasswordController {

    @Autowired
    SendOtpService sendOtpService;

    @Autowired
    CustomerService customerService;

    @GetMapping("/reset")
    public String showReset1(Model model) {

        model.addAttribute("resetForm1", new Forms());

        return "profile/reset-password-1";
    }

    @PostMapping("/reset")
    public String processReset1(Model model, Forms resetForm1, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("resetForm1", resetForm1);
        String purpose = "PR";
        Forms formPasswordReset = (Forms) session.getAttribute("resetForm1");
        CustomerDetailsResponsePayload customerDetails = customerService.getCustomerDetails(session, formPasswordReset.getMobileNumber());
        if (customerDetails.getResponseCode().equals("00")) {
            final SendOtpResponsePayload sendOtp = sendOtpService.sendOtp(session, purpose, formPasswordReset.getMobileNumber());
            if (sendOtp.getResponseCode().equals("00")) {
                return "redirect:/otp";
            } else {
                String customErrorMessage = sendOtp.getResponseMessage();
                redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
                return "redirect:/reset";
            }
        } else {
            String customErrorMessage = customerDetails.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/reset";
        }
    }

    @GetMapping("/otp")
    public String showOtp(Model model, HttpSession session) {
        Forms formPasswordReset = (Forms) session.getAttribute("resetForm1");
        model.addAttribute("mobileNumberForm", formPasswordReset);
        model.addAttribute("resetOtpForm", new Forms());

        return "profile/reset-otp";
    }


    @PostMapping("/otp")
    public String processOtp(Model model, Forms resetOtpForm, HttpSession session) {
        session.setAttribute("resetOtpForm", resetOtpForm);
        model.addAttribute("resetOtp", new Forms());

        return "redirect:/reset-2";
    }

    @GetMapping("/reset-2")
    public String showReset2(Model model, HttpSession session) {

        CustomerDetailsResponsePayload customer = (CustomerDetailsResponsePayload) session.getAttribute("customerDetailsResponse");
        model.addAttribute("resetForm2", new Forms());
        model.addAttribute("securityQuestion", customer);
        return "profile/reset-password-2";
    }

    @PostMapping("/reset-2")
    public String processReset2(Model model, Forms resetForm2, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {

        session.setAttribute("resetForm2", resetForm2);

        final GeneralResponsePayload resetPassword = customerService.resetPassword(session);
        if (resetPassword.getResponseCode().equals("00")) {
            return "redirect:/login";
        } else if (resetPassword.getResponseCode().equals("03")) {
            String customErrorMessage = resetPassword.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/otp";
        } else {
            String customErrorMessage = resetPassword.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/reset-2";
        }

    }

    @GetMapping("/profile")
    public String showProfile(Model model) {

//        model.addAttribute("resetForm2", new Forms());

        return "profile/profile";
    }

}
