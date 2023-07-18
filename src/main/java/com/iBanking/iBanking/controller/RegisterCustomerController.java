package com.iBanking.iBanking.controller;

import com.iBanking.iBanking.Forms.Forms;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponsePayload;
import com.iBanking.iBanking.payload.SendOtpResponsePayload;
import com.iBanking.iBanking.services.CustomerService;
import com.iBanking.iBanking.payload.customer.RegisterCustomerResponsePayload;
import com.iBanking.iBanking.services.SendOtpService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
public class RegisterCustomerController {
    @Autowired
    SendOtpService sendOtpService;
    @Autowired
    CustomerService customerService;
    @Autowired
    SendOtpService otpService;

    @GetMapping("/register")
    public String showRegister1(Model model) {
        model.addAttribute("registerForm1", new Forms());
        return "register/register-1";
    }

    @PostMapping("/register")
    public String processRegister1(@ModelAttribute Forms registerForm1, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("registerForm1", registerForm1);

        //CHECK IF CUSTOMER EXISTING FIRST
        Forms formMobileNumber = (Forms) session.getAttribute("registerForm1");
        CustomerDetailsResponsePayload customerDetails = customerService.getCustomerDetails(session, formMobileNumber.getMobileNumber());
        Forms registerForm11 = (Forms) session.getAttribute("registerForm1");
        if (customerDetails.getResponseCode().equals("03")) {
            String customErrorMessage = customerDetails.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/register";
        } else {
            if (customerDetails.getRegistered().equals("true")) {
                String customErrorMessage = "Customer with Mobile Number " + registerForm11.getMobileNumber() + " is already registered, kindly proceed to login ";
                redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
                return "redirect:/register";
            } else {
                String purpose = "RE";
                Forms formRegister = (Forms) session.getAttribute("registerForm1");
                SendOtpResponsePayload sendOtp = sendOtpService.sendOtp(session, purpose, formRegister.getMobileNumber());
                if (sendOtp.getResponseCode().equals("00")) {
                    return "redirect:/register/confirm-otp";
                } else {
                    String customErrorMessage = sendOtp.getResponseMessage();
                    redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
                    return "redirect:/register";
                }
            }
        }

    }

    @GetMapping("/register/confirm-otp")
    public String showRegister2(Model model, HttpSession session) {
        model.addAttribute("registerForm2", new Forms());

        Forms mobileNumberForm = (Forms) session.getAttribute("registerForm1");
        model.addAttribute("mobileNumberForm", mobileNumberForm);
        return "register/register-2";
    }

    @PostMapping("/register/confirm-otp")
    public String processRegister2(@ModelAttribute Forms registerForm2, HttpSession session) {
        session.setAttribute("registerForm2", registerForm2);

        return "redirect:/register/create-password";
    }

    @GetMapping("/register/create-password")
    public String showRegisterPassword(Model model) {
        model.addAttribute("registerFormPassword", new Forms());
        return "register/register-3-password";
    }

    @PostMapping("/register/create-password")
    public String processRegisterPassword(@ModelAttribute Forms registerFormPassword, HttpSession session) {
        session.setAttribute("registerFormPassword", registerFormPassword);
        return "redirect:/register/create-profile";
    }

    @GetMapping("/register/create-profile")
    public String showRegister3(Model model) {
        model.addAttribute("registerForm3", new Forms());
        return "register/register-3";
    }

    @PostMapping("/register/create-profile")
    @ResponseBody
    public String processRegister3(@ModelAttribute Forms registerForm3, HttpSession session, RedirectAttributes redirectAttributes) {
        session.setAttribute("registerForm3", registerForm3);
        try {
            customerService.registerCustomer(session);
            RegisterCustomerResponsePayload registerCustomerResponse = (RegisterCustomerResponsePayload) session.getAttribute("registerCustomerResponse");
            if (registerCustomerResponse.getResponseCode().equals("00")) {
                return "00";
            } else if (registerCustomerResponse.getResponseCode().equals("03")) {
                return "03";
            } else {
                return registerCustomerResponse.getResponseMessage();
            }
        } catch (Exception e) {
            log.info("Error while registering customer {}", e.getMessage());
            throw new RuntimeException("Error while registering customer", e);
        }

    }

    @PostMapping("/resend-otp-register")
    @ResponseBody
    public String sendOtp(HttpSession session) throws UnirestException {
        String purpose = "RE";
        Forms formCreate = (Forms) session.getAttribute("registerForm1");
        final SendOtpResponsePayload sendOtp = otpService.sendOtp(session, purpose, formCreate.getMobileNumber());
        return sendOtp.getResponseMessage();
    }
}
