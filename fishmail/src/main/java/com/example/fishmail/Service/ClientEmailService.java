// package com.example.fishmail.Service;

// import java.util.Properties;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.JavaMailSenderImpl;
// import org.springframework.mail.javamail.MimeMessageHelper;
// import org.springframework.stereotype.Service;

// import com.example.fishmail.Models.AccountModel;

// import jakarta.mail.MessagingException;
// import jakarta.mail.internet.MimeMessage;

// @Service
// public class ClientEmailService {
    
//     // @Autowired
//     // private JavaMailSender mailSender;

//     @Autowired
//     private AccountService accountService;



    
//     public void sendMimeMessage(String accountEmail) throws MessagingException{
//         AccountModel accountFromSend = accountService.findByEmail(accountEmail).orElseThrow(() -> new RuntimeException("Nie znaleziono konta!"));
//         MimeMessage message = mailSender.createMimeMessage();
//         MimeMessageHelper helper = new MimeMessageHelper(message,true);
        
        
//         helper.setFrom(accountFromSend.getSmtpLogin());
//         helper.setTo("kamilkarp22@gmail.com");
//         helper.setSubject("Testowy mail z załącznikiem");
//           helper.setText(
//             "<html><body>" +
//             "<p>" + "super" + "</p>" +
//             "<img src='http://localhost:9000/images/lightIcon.png'>" + // Wstaw obrazek inline
//             "</body></html>", true); // Ustawienie treści jako HTML

//             // Dodanie obrazu jako załącznika inline
//             // File file = new File("http://localhost:9000/images/lightIcon.png");
//             // helper.addInline("trackingImage", file);

//             // Wysyłanie wiadomości
//             mailSender.send(message);
//     }
// }

// <img src="https://twojadomena.com/track/opened?trackingId=UNIKALNE_ID" width="1" height="1" style="display:none;" />
