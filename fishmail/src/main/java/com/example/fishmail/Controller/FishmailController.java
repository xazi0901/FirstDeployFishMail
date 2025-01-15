package com.example.fishmail.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fishmail.RequestModels.EmailRequest;
import com.example.fishmail.Service.SendEmailService;

import jakarta.mail.MessagingException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class FishmailController {

    @Autowired
    SendEmailService sendEmailService;
    

    @GetMapping("/")
    public String getHome() throws MessagingException {
        // sendEmailService.sendMimeMessage("test@gmail.com");
        return "home";
    }

    @GetMapping("/zaloguj")
    public String getLoginPage() {
        return "login";
    }
    
    @GetMapping("/rejestracja")
    public String getRegisterPage() {
        return "register";
    }

    @GetMapping("/kontakt")
    public String getContactPage() {
        return "contact";
    }

    @GetMapping("/polityka-prywatnosci")
    public String getPrivatePolicyPage() {
        return "privacy-policy";
    }
    
    
    


    


    
}
