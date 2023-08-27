package com.iBanking.iBanking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iBanking.iBanking.Forms.TransactionForms;
import com.iBanking.iBanking.payload.customer.CustomerDetailsResponsePayload;
import com.iBanking.iBanking.payload.SendOtpResponsePayload;
import com.iBanking.iBanking.payload.customer.UpdateCustomerRequestPayload;
import com.iBanking.iBanking.payload.generics.GeneralResponsePayload;
import com.iBanking.iBanking.services.CustomerService;
import com.iBanking.iBanking.payload.customer.RegisterCustomerResponsePayload;
import com.iBanking.iBanking.services.SendOtpService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import static com.iBanking.iBanking.utils.Generics.encodeMultipartFileToBase64;

@Controller
@Slf4j
public class RegisterCustomerController {
    @Autowired
    SendOtpService sendOtpService;
    @Autowired
    CustomerService customerService;
    @Autowired
    SendOtpService otpService;
    @Autowired
    Gson gson;

    @GetMapping("/register")
    public String showRegister1(Model model) {
        model.addAttribute("registerForm1", new TransactionForms());
        return "register/register-1";
    }

    @GetMapping("/update-customer")
    public String updateCustomer(Model model, HttpSession session) {
        CustomerDetailsResponsePayload customerDetailsResponse = (CustomerDetailsResponsePayload) session.getAttribute("customerDetailsResponse");
        model.addAttribute("updateCustomer", customerDetailsResponse.getMissingFields());
        return "register/update-customer";
    }

    @PostMapping("/update-customer")
    public String processUpdateCustomer(@RequestParam Map<String, String> updateCustomerList, @RequestParam(name = "passportPhoto", required = false) MultipartFile passport,
                                        HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException, IOException {
        session.setAttribute("updateCustomerRequest", updateCustomerList);
        UpdateCustomerRequestPayload requestPayload = new UpdateCustomerRequestPayload();
        for (Map.Entry<String, String> entry : updateCustomerList.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("Key: " + key + " ,Value : " + value);
            if (key.contains("industry")) {
                requestPayload.setIndustry(value);
            } else if (key.contains("bvn")) {
                requestPayload.setBvn(value);
            } else if (key.contains("idType")) {
                requestPayload.setIdType(value);
            } else if (key.contains("idNumber")) {
                requestPayload.setIdNumber(value);
            } else if (key.contains("idImage")) {
                requestPayload.setIdImage(value);
            } else if (key.contains("passportPhoto") || passport.getSize() > 0) {
//                String s = encodeMultipartFileToBase64(passport);
                requestPayload.setPassportPhoto(value);
            } else if (key.contains("marital")) {
                requestPayload.setMaritalStatus(value);
            } else if (key.contains("sector")) {
                requestPayload.setSector(value);
            } else if (key.contains("email")) {
                requestPayload.setEmailAddress(value);
            }
            System.out.println(requestPayload);

        }
        GeneralResponsePayload updateCustomerDetails = customerService.updateCustomerDetails(session, requestPayload);
        System.out.println("Update Customer Details Response " + updateCustomerDetails);
        if (updateCustomerDetails.getResponseCode().equals("00")) {
            return "redirect:/register/confirm-otp";
        } else {
            String customErrorMessage = updateCustomerDetails.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/register/update-customer";
        }
    }

    @PostMapping("/register")
    public String processRegister1(@ModelAttribute TransactionForms registerForm1, HttpSession session, RedirectAttributes redirectAttributes) throws UnirestException {
        session.setAttribute("registerForm1", registerForm1);

        //CHECK IF CUSTOMER EXISTING FIRST
        TransactionForms formMobileNumber = (TransactionForms) session.getAttribute("registerForm1");
        CustomerDetailsResponsePayload customerDetails = customerService.getCustomerDetails(session, formMobileNumber.getMobileNumber());
        TransactionForms registerForm11 = (TransactionForms) session.getAttribute("registerForm1");
        if (!customerDetails.getResponseCode().equals("00")) {
            String customErrorMessage = customerDetails.getResponseMessage();
            redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
            return "redirect:/register";
        } else {
            if (customerDetails.getMissingFields().length > 1) {
                return "redirect:/update-customer";
            }
            if (customerDetails.getRegistered().equals("true")) {
                String customErrorMessage = "Customer with Mobile Number " + registerForm11.getMobileNumber() + " is already registered, kindly proceed to login ";
                redirectAttributes.addFlashAttribute("errorMessage", customErrorMessage);
                return "redirect:/register";
            } else {
                String purpose = "RE";
                TransactionForms formRegister = (TransactionForms) session.getAttribute("registerForm1");
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
        model.addAttribute("registerForm2", new TransactionForms());

        TransactionForms mobileNumberForm = (TransactionForms) session.getAttribute("registerForm1");
        model.addAttribute("mobileNumberForm", mobileNumberForm);
        return "register/register-2";
    }

    @PostMapping("/register/confirm-otp")
    public String processRegister2(@ModelAttribute TransactionForms registerForm2, HttpSession session) {
        session.setAttribute("registerForm2", registerForm2);

        return "redirect:/register/create-password";
    }

    @GetMapping("/register/create-password")
    public String showRegisterPassword(Model model) {
        model.addAttribute("registerFormPassword", new TransactionForms());
        return "register/register-3-password";
    }

    @PostMapping("/register/create-password")
    public String processRegisterPassword(@ModelAttribute TransactionForms registerFormPassword, HttpSession session) {
        session.setAttribute("registerFormPassword", registerFormPassword);
        return "redirect:/register/create-profile";
    }

    @GetMapping("/register/create-profile")
    public String showRegister3(Model model) {
        model.addAttribute("registerForm3", new TransactionForms());
        return "register/register-3";
    }

    @PostMapping("/register/create-profile")
    @ResponseBody
    public String processRegister3(@ModelAttribute TransactionForms registerForm3, HttpSession session, RedirectAttributes redirectAttributes) {
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
        TransactionForms formCreate = (TransactionForms) session.getAttribute("registerForm1");
        final SendOtpResponsePayload sendOtp = otpService.sendOtp(session, purpose, formCreate.getMobileNumber());
        return sendOtp.getResponseMessage();
    }
}
