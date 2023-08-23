package com.iBanking.iBanking.controller;

import com.iBanking.iBanking.Forms.TransactionForms;
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
public class ProfileController {

    @Autowired
    SendOtpService sendOtpService;

    @Autowired
    CustomerService customerService;

    @GetMapping("/reset")
    public String showReset1(Model model) {

        model.addAttribute("resetForm1", new TransactionForms());

        return "profile/reset-password-1";
    }

    @PostMapping("/reset")
    public String processReset1(Model model, TransactionForms resetForm1, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("resetForm1", resetForm1);
        String purpose = "PR";
        TransactionForms formPasswordReset = (TransactionForms) session.getAttribute("resetForm1");
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
        TransactionForms formPasswordReset = (TransactionForms) session.getAttribute("resetForm1");
        model.addAttribute("mobileNumberForm", formPasswordReset);
        model.addAttribute("resetOtpForm", new TransactionForms());

        return "profile/reset-otp";
    }


    @PostMapping("/otp")
    public String processOtp(Model model, TransactionForms resetOtpForm, HttpSession session) {
        session.setAttribute("resetOtpForm", resetOtpForm);
        model.addAttribute("resetOtp", new TransactionForms());

        return "redirect:/reset-2";
    }

    @GetMapping("/reset-2")
    public String showReset2(Model model, HttpSession session) {

        CustomerDetailsResponsePayload customer = (CustomerDetailsResponsePayload) session.getAttribute("customerDetailsResponse");
        model.addAttribute("resetForm2", new TransactionForms());
        model.addAttribute("securityQuestion", customer);
        return "profile/reset-password-2";
    }

    @PostMapping("/reset-2")
    public String processReset2(Model model, TransactionForms resetForm2, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {

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

//        model.addAttribute("resetForm2", new TransactionForms());

        return "profile/profile";
    }


//    *********************************************** RESET OTP

    @GetMapping("/reset-pin")
    public String showResetOtp1(Model model) {

        model.addAttribute("pinResetForm1", new TransactionForms());

        return "profile/reset-pin-1";
    }

    @PostMapping("/reset-pin")
    public String processResetOtp1(Model model, TransactionForms pinResetForm1, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("pinResetForm1", pinResetForm1);
        String purpose = "PI";
        TransactionForms formPinReset = (TransactionForms) session.getAttribute("pinResetForm1");
        CustomerDetailsResponsePayload customerDetails = customerService.getCustomerDetails(session, formPinReset.getMobileNumber());
        if (customerDetails.getResponseCode().equals("00")) {
            final SendOtpResponsePayload sendOtp = sendOtpService.sendOtp(session, purpose, formPinReset.getMobileNumber());
            if (sendOtp.getResponseCode().equals("00")) {
                return "redirect:/otp-pin";
            } else {
                String customErrorMessage = sendOtp.getResponseMessage();
                redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
                return "redirect:/reset-pin";
            }
        } else {
            String customErrorMessage = customerDetails.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/reset-pin";
        }
    }

    @GetMapping("/otp-pin")
    public String showPinOtp(Model model, HttpSession session) {
        TransactionForms formPasswordReset = (TransactionForms) session.getAttribute("pinResetForm1");
        model.addAttribute("mobileNumberForm", formPasswordReset);
        model.addAttribute("resetOtpForm", new TransactionForms());

        return "profile/reset-pin-otp";
    }


    @PostMapping("/otp-pin")
    public String processOtpPin(Model model, TransactionForms resetOtpPinForm, HttpSession session) {
        session.setAttribute("resetOtpPinForm", resetOtpPinForm);
        model.addAttribute("resetOtp", new TransactionForms());

        return "redirect:/reset-pin-2";
    }

    @GetMapping("/reset-pin-2")
    public String showResetPin2(Model model, HttpSession session) {

        CustomerDetailsResponsePayload customer = (CustomerDetailsResponsePayload) session.getAttribute("customerDetailsResponse");
        model.addAttribute("resetForm2", new TransactionForms());
        model.addAttribute("securityQuestion", customer);
        return "profile/reset-pin-2";
    }

    @PostMapping("/reset-pin-2")
    public String processResetPin2(Model model, TransactionForms resetForm2, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {

        session.setAttribute("resetPinForm2", resetForm2);

        final GeneralResponsePayload resetPin = customerService.resetPin(session);
        if (resetPin.getResponseCode().equals("00")) {
            return "redirect:/dashboard";
        } else if (resetPin.getResponseCode().equals("03")) {
            String customErrorMessage = resetPin.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/otp";
        } else {
            String customErrorMessage = resetPin.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/reset-pin-2";
        }

    }

}
