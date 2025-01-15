package com.example.fishmail.Consumers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.fishmail.Config.ClientEmailConfiguration;
import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.EmailDTO;
import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Models.OutgoingBook;
import com.example.fishmail.Models.OutgoingBookDTO;
import com.example.fishmail.Models.SendingModel;
import com.example.fishmail.Models.Enum.SendingStatus;
import com.example.fishmail.Repository.OutgoingBookRepository;
import com.example.fishmail.Service.AccountService;
import com.example.fishmail.Service.CampaingService;
import com.example.fishmail.Service.EmailService;
import com.example.fishmail.Service.OutgoingBookConverter;
import com.example.fishmail.Service.OutgoingBookService;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailConsumer {
    
    @Autowired
    private AccountService accountService;
    @Autowired
    private CampaingService campaingService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ClientEmailConfiguration emailConfiguration;
    @Autowired
    private OutgoingBookConverter outgoingBookConverter;
    @Autowired
    private OutgoingBookRepository outgoingBookRepository;
    @Autowired
    private OutgoingBookService outgoingBookService;


// ODWRÓCENIE
    @RabbitListener(queues = "email-queue")
    public void processEmail(OutgoingBookDTO outgoingBookDTO){

        try {
        EmailModel email = emailService.findOneById(String.valueOf(outgoingBookDTO.getEmailID()));
        CampaignModel campaignModel = email.getCampaign();
        AccountModel sender = accountService.findByEmail(campaignModel.getAccount().getEmail()).orElseThrow(() -> new RuntimeException("Nie znaleziono konta!"));
        
        JavaMailSender mailSender = emailConfiguration.getJavaMailSender(sender.getEmail());
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
    // Wygeneruj dynamiczny URL
    String baseUrl = "https://fishmail.fishweb.pl/track/opened";
    String src = baseUrl + "?trackingId=" + URLEncoder.encode(outgoingBookDTO.getTrackingId(), StandardCharsets.UTF_8) + "&campaingId=" +  URLEncoder.encode(String.valueOf(campaignModel.getId()), StandardCharsets.UTF_8);

    // HTML z dynamicznym src
    String emailContent = String.format(
        "<html>" +
        "<body>" + email.getMessageBody() +
        "<img src='%s' width='1' height='1' style='display:none;' />" +
        "</body>" +
        "</html>",
        src
    );
        helper.setFrom(sender.getSmtpLogin());
        helper.setTo(outgoingBookDTO.getRecivierEmail());
        helper.setSubject(email.getTitle());
        //   helper.setText(
        //     "<html><body>" + email.getMessageBody() +
        //     "<p>" + "super" + "</p>" +
        //    "<img src='s'" + outgoingBookDTO.getTrackingId() + "'&campaingId='" +campaignModel.getId() + "' width='1' height='1' style='display:none;' />" + // Wstaw obrazek inline
        //     "</body></html>", true);
          helper.setText(emailContent, true); // true -> tekst HTML
            // Wysyłanie wiadomości
            mailSender.send(message);
            outgoingBookDTO.setStatuses(SendingStatus.WYSŁANO);
            
            // OutgoingBook outgoingBookStatusToSave = outgoingBookConverter.FromDTOtoEnum(outgoingBookDTO);
            // outgoingBookRepository.save(outgoingBookStatusToSave);
            outgoingBookService.updateOutGoingBookAndCheckEmailStatus(outgoingBookDTO);
        } catch (Exception e) {
            System.err.println("Błąd podczas wysyłania e-maila: " + e.getMessage());
            // Dajemy status w DTO
            outgoingBookDTO.setStatuses(SendingStatus.BŁĄD);
            // OutgoingBook outgoingBookStatusToSave = outgoingBookConverter.FromDTOtoEnum(outgoingBookDTO);
            // outgoingBookRepository.save(outgoingBookStatusToSave);
            outgoingBookService.updateOutGoingBookAndCheckEmailStatus(outgoingBookDTO);
        }
    }
}
