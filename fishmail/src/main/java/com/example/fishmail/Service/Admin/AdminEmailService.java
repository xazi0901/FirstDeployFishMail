package com.example.fishmail.Service.Admin;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.fishmail.Config.AdminEmailConfiguration;
import com.example.fishmail.Config.ClientEmailConfiguration;
import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Models.AccountRegistrationLinks;
import com.example.fishmail.Models.AdminEmail;
import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Models.Enum.SendingStatus;
import com.example.fishmail.Repository.AccountRegistrationLinksRepository;

import jakarta.mail.internet.MimeMessage;

@Service
@Async
public class AdminEmailService {

    @Autowired
    private AdminEmailConfiguration adminEmailConfiguration;
    @Autowired
    private AccountRegistrationLinksRepository accountRegistrationLinksRepository;
    @Autowired
    private AdminService adminService;


    public void sendRegisterEmail(AccountRegistrationLinks link){
        System.out.println("WYWOŁANO ADMINEMAILSERVICE");
    try {

        AccountRegistrationLinks linkToAccount = accountRegistrationLinksRepository.findOneByActivationCode(link.getActivationCode());
        System.out.println(link.getActivationCode());
        System.out.println(link.getAccount().getEmail());
        
        // AccountModel accountRecieversLink = adminService.findOneByRegistrationLink(linkToAccount);
        
        JavaMailSender mailSender = adminEmailConfiguration.getJavaMailSender();
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
    // HTML z dynamicznym src
    String emailContent = String.format(
        "<html>" +
        "<body>" +
        "<p>Witaj,</p>" +
        "<p>Dziękujemy za zaufanie dla produktu FishMail!</p>" +
        "<p>Mamy nadzieje, że nasze rozwiązanie przeznaczone do e-commerce, poszerzy znacznie grono twoich klientów!</p>" +
        "<p>Poniżej przesyłamy link aktywacyjny dla konta w serwisie FishMail. Pamiętaj o tym, że aktywacyjny jest aktywny przez 12 godzin!</p>" +
        "<a href='http://localhost:9000/aktywuj-konto?activationId=" + link.getActivationCode() +"'>Aktywuj konto</a>" +
        "<p>W przypadku przekroczenia czasu ważności linku aktywacyjnego kliknij w link poniżej, aby poprosić o przysłanie nowego.</p>" +
        "<a href='http://localhost:9000/wyslij-link-aktywacyjny'>Poproś o wysłanie linku aktywacyjnego raz jeszcze</a>" +
        "<p>Pozdrawiamy,</p>" +
        "<p>Ekipa Fishweb</p>" +
        "</body>" +
        "</html>"
    );
        helper.setFrom("ncleantest@nclean.pl");
        System.out.println(link.getAccount().getEmail());
        helper.setTo(link.getAccount().getEmail());
        helper.setSubject("Serwis fishmail, aktywuj konto!");
        helper.setText(emailContent, true); // true -> tekst HTML
        // Wysyłanie wiadomości
        mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Błąd podczas wysyłania e-maila: " + e.getMessage());
            // Dajemy status w DTO
        }
        
    }

    public void sendMessagesToAllFishmailUsers(AdminEmail adminEmail){
        try{
            List<AccountModel> listOfRecievers = adminService.getAllAccounts();
            List<String> adresses = new ArrayList<>();

            for(AccountModel adress : listOfRecievers){
                adresses.add(adress.getEmail());
            }

            JavaMailSender mailSender = adminEmailConfiguration.getJavaMailSender();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");

            for(String sendingReciever : adresses){
                helper.setFrom("ncleantest@nclean.pl");
                helper.setTo(sendingReciever);
                helper.setSubject(adminEmail.getTitle());
                helper.setText(adminEmail.getMessageBody());
                mailSender.send(message);
            }
        } catch (Exception e){
            System.out.println("Błąd podczas wysłania wiadmości" + e.getMessage());
        }
    }

    public void sendMessageToAccountUserValidateTime(List<String> recievers){
        try{
            JavaMailSender mailSender = adminEmailConfiguration.getJavaMailSender();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
                // HTML z dynamicznym src
    String emailContent = String.format(
        "<html>" +
        "<body>" +
        "<p>Witaj,</p>" +
        "<p>Ważność twojego konta na platformie Fishmail dobiega końca..</p>" +
        "<p>Prosimy o uregulowanie rachunku abyś mógł korzystać z platformy nieprzerwanie!</p>" +
        "<p>Pozdriawiamy,</p>" +
        "<p>Ekipa Fishweb</p>" +
        "</body>" +
        "</html>"
    );
            for(String reciever : recievers){
            helper.setFrom("ncleantest@nclean.pl");
            helper.setTo(reciever);
            helper.setCc("ncleantest@nclean.pl");
            helper.setText(emailContent);
            helper.setSubject("Fishmail Twoje konto traci ważność!");
            mailSender.send(message);
            }


        } catch (Exception e) {
            System.out.println("Błąd podczas wysyłania wiadomości:" +e.getMessage());
        }
    }
    
}
