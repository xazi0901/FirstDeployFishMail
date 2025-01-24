package com.example.fishmail.Config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Service.AccountService;

@Service
public class AdminEmailConfiguration {
    @Autowired
    private AccountService accountService;

        // Funkcja która pobiera dane użytkownika do SMTP
        public JavaMailSender getJavaMailSender(){
        AccountModel accountFromSend = accountService.findByEmail("kkarpix9020@gmail.com").orElseThrow(() -> new RuntimeException("Nie znaleziono konta admina!"));
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(accountFromSend.getSmtpHost());
        mailSender.setPort(accountFromSend.getSmtpPort());
        mailSender.setUsername(accountFromSend.getSmtpLogin());
        mailSender.setPassword(accountFromSend.getSmtpPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("spring.mail.properties.mail.smtp.starttls.required","true");
        props.put("mail.debug", "true");

        return mailSender;

    }
}
