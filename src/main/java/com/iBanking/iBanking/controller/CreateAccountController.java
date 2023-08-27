package com.iBanking.iBanking.controller;


import com.iBanking.iBanking.Forms.TransactionForms;
import com.iBanking.iBanking.payload.SendOtpResponsePayload;
import com.iBanking.iBanking.payload.customer.CreateCustomerResponsePayload;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponsePayload;
import com.iBanking.iBanking.services.CustomerService;
import com.iBanking.iBanking.services.SendOtpService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;

import static com.iBanking.iBanking.utils.Generics.encodeMultipartFileToBase64;

@Slf4j
@Controller
public class CreateAccountController {

    @Autowired
    SendOtpService otpService;
    @Autowired
    CustomerService customerService;

    @GetMapping("/open-account")
    public String showCreateAccount1(Model model) {
        model.addAttribute("createAccountForm1", new TransactionForms());
        return "create-account/create-account-1";
    }

    @PostMapping("/open-account")
    public String processCreateAccount1(Model model, TransactionForms createAccountForm1, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("createAccountForm1", createAccountForm1);
        TransactionForms mobileNumberForm = (TransactionForms) session.getAttribute("createAccountForm1");
        CustomerDetailsResponsePayload customerDetails = customerService.getCustomerDetails(session, mobileNumberForm.getMobileNumber());
        if (customerDetails.getResponseCode().equals("00")) {
            String customErrorMessage = "Customer exists already, create a profile or proceed to login";
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/open-account";
        } else {

            return "redirect:/open-account-2";
        }
    }

    @GetMapping("/confirm-otp")
    public String showConfirmOtp(Model model, HttpSession session) {
        model.addAttribute("confirmOtp", new TransactionForms());
        TransactionForms mobileNumberForm = (TransactionForms) session.getAttribute("createAccountForm1");
        model.addAttribute("mobileNumberForm", mobileNumberForm);
        return "create-account/create-account-otp";
    }

    @PostMapping("/confirm-otp")
    public String processConfirmOtp(@RequestParam("otp") String otp,
                                    Model model, TransactionForms confirmOtp, HttpSession session) {
        session.setAttribute("confirmOtp", otp);

        return "redirect:/open-account-2";
    }

    @GetMapping("/open-account-2")
    public String showCreateAccount2(Model model) {
        model.addAttribute("createAccountForm2", new TransactionForms());
        return "create-account/create-account-2";
    }

    @PostMapping("/open-account-2")
//    @ResponseBody
    public String processCreateAccount2(@RequestParam("passport") MultipartFile passport,
                                        TransactionForms createAccountForm2, HttpSession session, RedirectAttributes redirectAttributes) throws IOException, UnirestException {
        session.setAttribute("createAccountForm2", createAccountForm2);
        String passportBase64 = encodeMultipartFileToBase64(passport);
        session.setAttribute("passportSession", passportBase64);
        customerService.createCustomer(session);
        final CreateCustomerResponsePayload customer = customerService.createCustomer(session);
        if (customer.getResponseCode().equals("00")) {
            return "redirect:/register";
        } else {
            String customErrorMessage = customer.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/open-account-2";
        }
    }

    @GetMapping("/open-account-3")
    public String showCreateAccount3(Model model) {
        model.addAttribute("createAccountForm3", new TransactionForms());
        return "create-account/create-account-3";
    }

    @PostMapping("/open-account-3")
    public String processCreateAccount3(@RequestParam("passport") MultipartFile passport,
                                        @RequestParam("signature") MultipartFile signature,
                                        @RequestParam("utility") MultipartFile utility,
                                        Model model, TransactionForms createAccountForm3, HttpSession session) throws IOException {
        session.setAttribute("createAccountForm3", createAccountForm3);

        try {
            String passportBase64 = processImageToBase64(passport);
            String signatureBase64 = processImageToBase64(signature);
            String utilityBase64 = processImageToBase64(utility);
            session.setAttribute("passportSession", passportBase64);
            session.setAttribute("signatureSession", signatureBase64);
            session.setAttribute("utilitySession", utilityBase64);
//            log.info("PASSPORT BASE 64 STRING : {}", passportBase64);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/open-account-4";
    }

    @GetMapping("/open-account-4")
    public String showCreateAccount4(Model model) {
        model.addAttribute("createAccountForm4", new TransactionForms());
        return "create-account/create-account-4";
    }

    @PostMapping("/open-account-4")
    public String processCreateAccount4(Model model, HttpSession session) throws UnirestException {
//        session.setAttribute("createAccountForm4", createAccountForm4);
//        final CreateCustomerResponsePayload customer = customerService.createCustomer(session);
        return "00";
    }

    @PostMapping("/create-account")
    @ResponseBody
    public String CreateAccount(@RequestParam("employerStatus") String employerStatus,
                                @RequestParam("employerName") String employerName,
                                @RequestParam("employerAddress") String employerAddress,
                                @RequestParam("employerCity") String employerCity,
                                @RequestParam("occupation") String occupation,
                                @RequestParam("employmentDate") String employmentDate,
                                @RequestParam("retirementDate") String retirementDate,
                                @RequestParam("referredBy") String referredBy,
                                @RequestParam("otp") String otp,
                                TransactionForms createAccountForm4, HttpSession session) throws UnirestException {
        session.setAttribute("createAccountForm4", createAccountForm4);
        session.setAttribute("createOtpLastPage", otp);
        final CreateCustomerResponsePayload customer = customerService.createCustomer(session);
        if (customer.getResponseCode().equals("00")) {
            return "00";
        } else if (customer.getResponseCode().equals("03")) {
            return customer.getResponseCode();
        } else {
            return customer.getResponseMessage();
        }

    }

    private String processImageToBase64(MultipartFile image) throws IOException, IOException {
        String base64Image = "";
        if (!image.isEmpty()) {
            byte[] imageBytes = image.getBytes();
            // Convert the image bytes to a base64 string
            base64Image = Base64.getEncoder().encodeToString(imageBytes);
            return base64Image;
        }
        return base64Image;
    }

    //This is used to resend OTP
    @PostMapping("/resend-otp")
    @ResponseBody
    public String sendOtp(HttpSession session) throws UnirestException {
        String purpose = "AO";
        TransactionForms formCreate = (TransactionForms) session.getAttribute("createAccountForm1");
        final SendOtpResponsePayload sendOtp = otpService.sendOtp(session, purpose, formCreate.getMobileNumber());
        return sendOtp.getResponseMessage();
    }
}
