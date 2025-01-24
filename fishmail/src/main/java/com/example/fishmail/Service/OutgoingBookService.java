package com.example.fishmail.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Models.OutgoingBook;
import com.example.fishmail.Models.OutgoingBookDTO;
import com.example.fishmail.Models.RecieversModel;
import com.example.fishmail.Models.Enum.EmailStatus;
import com.example.fishmail.Models.Enum.SendingStatus;
import com.example.fishmail.Repository.OutgoingBookRepository;

@Service
public class OutgoingBookService {
    
    @Autowired
    private OutgoingBookRepository outgoingBookRepository;
    @Autowired
    private CampaingService campaingService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private OutgoingBookConverter outgoingBookConverter;


    // 1. Weryfikujemy czy konto posiada tą kampanie i ją pobieramy
    // 2. Z tej kampanii wyciągamy email który ma być wysłany
    // 3. Do każdej wiadmości zapisujemy odbiorce wraz z url

            public void saveOutgoingBookWithoutObject(String campaingId) {
            CampaignModel campaing = campaingService.findOneById(campaingId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
            List<EmailModel> campaingEmails = campaing.getEmails();
            List<RecieversModel> campaingRecieverList = campaing.getRecivers();
            // Iterujemy po wszystkich odbiorcach
            for (RecieversModel sendingEmail : campaingRecieverList) {
                // Dla każdego odbiorcy przypisujemy wszystkie emaile
                for (EmailModel emailModel : campaingEmails) {
                    OutgoingBook outgoingBookToSave = new OutgoingBook();
                    outgoingBookToSave.setStatus(SendingStatus.ZAPLANOWANA);
                    outgoingBookToSave.setEmail(emailModel);
                    outgoingBookToSave.setRecivierEmail(sendingEmail.getRecieverEmail());
                    outgoingBookToSave.setTrackingId(UUID.randomUUID().toString());
                    outgoingBookToSave.setSendTime(emailModel.getSendTime());
                    outgoingBookToSave.setSendDate(emailModel.getSendDate());

                    outgoingBookRepository.save(outgoingBookToSave);
                }
            }
        }

        public void saveOutgoingBookWithOutgoingBookObject(OutgoingBook outgoingBook, String campaingId) {
            CampaignModel campaing = campaingService.findOneById(campaingId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
            List<EmailModel> campaingEmails = campaing.getEmails();
            List<RecieversModel> campaingRecieverList = campaing.getRecivers();
            // Iterujemy po wszystkich odbiorcach
            for (RecieversModel sendingEmail : campaingRecieverList) {
                // Dla każdego odbiorcy przypisujemy wszystkie emaile
                for (EmailModel emailModel : campaingEmails) {
                    OutgoingBook outgoingBookToSave = new OutgoingBook();
                    outgoingBookToSave.setEmail(emailModel);
                    outgoingBookToSave.setRecivierEmail(sendingEmail.getRecieverEmail());
                    outgoingBookToSave.setTrackingId(UUID.randomUUID().toString());

                    outgoingBookRepository.save(outgoingBookToSave);
                }
            }
        }

        // Funkcja dla zmiany statusu isOpened dla emaila
        public void changeIsOpenedStatusForEmail(String trackingId, String campaingId){
            // Szukamy model OutgoingBook po trackingId
             OutgoingBook outgoingBookToChange = outgoingBookRepository.findByTrackingId(trackingId).orElseThrow(() -> new RuntimeException("Nie znaleziono rekordu w książce wysyłek!"));
             // Pobieramy wiadomość email z rekordu aby sprawdzić czy zgadza się z campaingId
             EmailModel emailModelToCheck = outgoingBookToChange.getEmail();
             // Sprawdzamy zgodność
            //  CampaignModel campaignModel = emailModelToCheck.getCampaign().getId();
             if(!emailModelToCheck.getCampaign().getId().equals(Long.valueOf(campaingId))) throw new RuntimeException("Błąd walidacji! Wskazany email nie należy do kampanii z url!");
             // Zmieniamy Status tego rekordu
             outgoingBookToChange.setOpened(true);
             // Zapisujemy
             outgoingBookRepository.save(outgoingBookToChange);
        }


        public List<OutgoingBook> getAllOutgoingBooksForEmailCampaing(String id ){
                try {
        Long emailId = Long.parseLong(id); // Walidacja ID
        return outgoingBookRepository.findAllByEmailId(emailId);
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid ID: " + id);
    }
            //   System.out.println("Received ID: " + id);
            // List<OutgoingBook> outgoingBookForEmail = outgoingBookRepository.findAllByEmailId(Long.parseLong(id));
            // return outgoingBookForEmail;
        }

        


        public void updateOutGoingBookAndCheckEmailStatus(OutgoingBookDTO outgoingBookDTO){
            System.out.println(outgoingBookDTO.getEmailID());
            OutgoingBook outgoingBook = outgoingBookConverter.FromDTOtoEnum(outgoingBookDTO);
            EmailModel emailModelToSaveInOutgoingBook = emailService.findOneById(String.valueOf(outgoingBookDTO.getEmailID()));
            outgoingBook.setEmail(emailModelToSaveInOutgoingBook);

            outgoingBookRepository.save(outgoingBook);

               if (emailModelToSaveInOutgoingBook == null) {
        throw new RuntimeException("EmailModel jest null w OutgoingBook!");
    }
            CampaignModel campaignModel = emailModelToSaveInOutgoingBook.getCampaign();
                if (campaignModel == null) {
        throw new RuntimeException("CampaignModel jest null w EmailModel!");
    }
            List<OutgoingBook> listOfOutgoingBooks = outgoingBookRepository.findAllByEmail(emailModelToSaveInOutgoingBook);

            boolean allCompleted = listOfOutgoingBooks.stream().allMatch(book -> book.getStatus() == SendingStatus.WYSŁANO || book.getStatus() == SendingStatus.BŁĄD);

            if(allCompleted){
                emailModelToSaveInOutgoingBook.setCampaign(campaignModel);
                emailModelToSaveInOutgoingBook.setStatus(EmailStatus.ZAKOŃCZONA);
                emailService.saveVoidEmail(emailModelToSaveInOutgoingBook);
        }

    }

    public List<OutgoingBook> getAllOutgoingBookWithCampaing(EmailModel emailModel){
        return outgoingBookRepository.findAllByEmail(emailModel);
    }
        
}
