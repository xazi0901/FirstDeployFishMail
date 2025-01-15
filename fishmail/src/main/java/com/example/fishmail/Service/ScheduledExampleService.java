package com.example.fishmail.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Models.EmailDTO;
import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Models.OutgoingBook;
import com.example.fishmail.Models.OutgoingBookDTO;
import com.example.fishmail.Models.Enum.EmailStatus;
import com.example.fishmail.Models.Enum.SendingStatus;
import com.example.fishmail.Repository.AccountRepository;
import com.example.fishmail.Repository.EmailRepository;
import com.example.fishmail.Repository.OutgoingBookRepository;
import com.example.fishmail.Service.Admin.AdminEmailService;
import com.example.fishmail.Service.Admin.AdminService;




// TUTAJ WPISZEMY FUNKCJE SYSTEMOWE KTÓRE MAJĄ ROBIĆ COŚ CYKLICZNIE PODCZAS DZIAŁANIA APLIKACJI. FUNKCJE "SYSTEMOWE!"
@Service
public class ScheduledExampleService {
   // Oznacz metodę, którą chcesz wywoływać o określonej porze, adnotacją @Scheduled. Możesz użyć różnych parametrów, takich jak cron, fixedRate, czy fixedDelay, w zależności od potrzeb.
    @Autowired
    private OutgoingBookRepository outgoingBookRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AdminEmailService adminEmailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OutgoingBookConverter outgoingBookConverter;
    // @Scheduled(cron = "15 8 16 ? * WED", zone = "Europe/Warsaw")
// Wartość w polu cron:
// 0 0 12 * * ? oznacza, że metoda zostanie wywołana codziennie o godzinie 12:00.
// Składnia cron:
// second (sekundy) – 0-59
// minute (minuty) – 0-59
// hour (godziny) – 0-23
// day of month (dzień miesiąca) – 1-31
// month (miesiąc) – 1-12 lub JAN-DEC
// day of week (dzień tygodnia) – 0-7 (0 i 7 oznacza niedzielę) lub MON-SUN
// ? – dowolny dzień tygodnia lub miesiąca
//Jeśli chcesz, aby harmonogram działał w określonej strefie czasowej, możesz dodać parametr zone:
    // public void scheduleExample(){
    //     System.out.println("Zostałem wywołany schedule Example!");
    // }

    
    // Kreuje się potrzeba na obiekt który będzie zawierał w sumie email wraz z trackingID

    @Scheduled(fixedRate = 60000)
    public void scheduleEmailsForSending(){
        System.out.println("Wywołano schedule!");
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        List<OutgoingBook> emailsToSend = outgoingBookRepository.findOutgoingBooksToSend(currentDate, currentTime);
        for(OutgoingBook email : emailsToSend){
            email.setStatus(SendingStatus.W_TRAKCIE);
            OutgoingBookDTO emailDto = outgoingBookConverter.toDTO(email);
            rabbitTemplate.convertAndSend("email-exchange", "email-routing-key",emailDto);
            outgoingBookRepository.save(email);
        }

        // Zastosujemy odwrócenie
        
    }


    @Scheduled(cron = "0 0 6 * * *", zone = "Europe/Warsaw")
    public void CheckUserAccountValidity(){
        System.out.println("Wywołano sprawdzanie kont uzytkowników");
        LocalDateTime now = LocalDateTime.now();
        List<AccountModel> accountToSend = accountRepository.findAccountsByValidAccountTime(now);
        List<String> recieversStringList = new ArrayList<>();
        for(AccountModel admin : accountToSend){
            // adminEmailService.
            recieversStringList.add(admin.getEmail());
        }
        adminEmailService.sendMessageToAccountUserValidateTime(recieversStringList);
    }

}
