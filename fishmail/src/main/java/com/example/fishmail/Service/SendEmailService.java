package com.example.fishmail.Service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.fishmail.Config.ClientEmailConfiguration;
import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.RequestModels.EmailRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class SendEmailService {
    
  @Autowired
  private AccountService accountService;
    // @Autowired
    // private JavaMailSender mailSender;
  @Autowired
  private ClientEmailConfiguration emailConfiguration;



    // public void sendMimeMessage(String accountEmail) throws MessagingException{
    //     AccountModel sender = accountService.findByEmail(accountEmail).orElseThrow(() -> new RuntimeException("Nie znaleziono podanego konta!"));
    //     JavaMailSender mailSender = emailConfiguration.getJavaMailSender(sender.getEmail());
    //     MimeMessage message = mailSender.createMimeMessage();
    //     MimeMessageHelper helper = new MimeMessageHelper(message,true);

    //     helper.setFrom("\"POLONARKO\" <ncleantest@nclean.pl>");
    //     helper.setTo("kamilkarp22@gmail.com");
    //     helper.setSubject("Testowy mail z załącznikiem");
    //       helper.setText(
    //         "<html><body>" +
    //         "<p>" + "super" + "</p>" +
    //         "<img src='http://localhost:9000/images/lightIcon.png'>" + // Wstaw obrazek inline
    //         "</body></html>", true); // Ustawienie treści jako HTML

    //         // Dodanie obrazu jako załącznika inline
    //         // File file = new File("http://localhost:9000/images/lightIcon.png");
    //         // helper.addInline("trackingImage", file);

    //         // Wysyłanie wiadomości
    //         mailSender.send(message);
    // }
}
