package com.example.fishmail.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Models.OutgoingBook;
import com.example.fishmail.Models.RecieversModel;
import com.example.fishmail.Models.Enum.EmailStatus;
import com.example.fishmail.Models.Enum.SendingStatus;
import com.example.fishmail.Repository.CampaingRepository;
import com.example.fishmail.Repository.EmailRepository;
import com.example.fishmail.Repository.OutgoingBookRepository;
import com.example.fishmail.Service.CampaingService;
import com.example.fishmail.Service.OutgoingBookService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class EmailController {
    
    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private CampaingService campaingService;

    @Autowired
    private CampaingRepository campaingRepository;

    @Autowired
    private OutgoingBookService outgoingBookService;

    @Autowired
    private OutgoingBookRepository outgoingBookRepository;

    // Wyświetl dany email z kampanii
    @GetMapping("/kampania/email/{id}")
    public String getEmailInCampaing(@PathVariable String id, HttpServletRequest request, Model model) {
        Principal userPrincipal = request.getUserPrincipal();
        EmailModel emailToShow = emailRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("Nie znaleziono wiadomości email"));
        model.addAttribute("loggedUser", userPrincipal);
        model.addAttribute("emailToShow", emailToShow);

        return "user-campaing-email";

    }

    // Wyświetl formularz dodawania emaili z panelu wybranej kampanii
    @GetMapping("/kampania/dodaj-email/{id}")
    public String getEmailFormToAdd(@PathVariable String id, Model model, HttpServletRequest request) {
        CampaignModel campaignToAddEmail = campaingRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
        Principal userPrincipal = request.getUserPrincipal();
        EmailModel emailToAdd = new EmailModel();
        model.addAttribute("campaingId", String.valueOf(campaignToAddEmail.getId()));
        model.addAttribute("loggedUser", userPrincipal);
        model.addAttribute("emailToAdd", emailToAdd);
        return "user-campaing-email-add";
    }
    
    // Dodaj maila do kmapanii
@PostMapping("/kampania/zapisz-email/{id}")
@Transactional
public String addEmailToCampaign(
        @PathVariable String id,
        @ModelAttribute EmailModel emailModel,
        HttpServletRequest request) {

    // Get the logged-in user
    Principal userPrincipal = request.getUserPrincipal();

    // Fetch the campaign and validate ownership
    CampaignModel campaignToAdd = campaingService.findOneById(id)
            .orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii!"));

    if (!userPrincipal.getName().equals(campaignToAdd.getAccount().getEmail())) {
        return "unauthorized";
    }

    // Debugowanie ID emaila
    System.out.println("Email ID przed ustawieniem na null: " + emailModel.getId());
    emailModel.setId(null);
    System.out.println("Email ID po ustawieniu na null: " + emailModel.getId());

    // Zapisz email w bazie danych
    emailModel.setCampaign(campaignToAdd);
    emailModel.setStatus(EmailStatus.W_TRAKCIE);
    emailModel = emailRepository.save(emailModel); // Zapisanie i nadpisanie z ID

    // Dodaj email do kampanii
    campaignToAdd.getEmails().add(emailModel);

    // Utwórz wpisy w OutgoingBook dla odbiorców
    List<RecieversModel> campaignRecievers = campaignToAdd.getRecivers();
    for (RecieversModel reciever : campaignRecievers) {
        OutgoingBook newOutgoingBook = new OutgoingBook();
        String trackingId = UUID.randomUUID().toString();
        newOutgoingBook.setEmail(emailModel); // emailModel już zapisany, ma ID
        newOutgoingBook.setSendDate(emailModel.getSendDate());
        newOutgoingBook.setSendTime(emailModel.getSendTime());
        newOutgoingBook.setStatus(SendingStatus.ZAPLANOWANA);
        newOutgoingBook.setRecivierEmail(reciever.getRecieverEmail());
        newOutgoingBook.setTrackingId(trackingId);

        // Zapisz wpis w OutgoingBook
        outgoingBookRepository.save(newOutgoingBook);
    }

    // Zapisz kampanię
    campaingRepository.save(campaignToAdd);

    return "redirect:/kampania/" + id;
}


    // Procesuj edycje pojedyńczego maila z kampanii
    @PostMapping("/kampania/email-edytuj/{id}")
    public String changeEmailInCapaing(@PathVariable String id,@ModelAttribute EmailModel emailModel, HttpServletRequest request, Model model) {
        Principal usePrincipal = request.getUserPrincipal();
        EmailModel emailToChange = emailRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("Nie znaleziono wiadomości email"));
        String campaingId = String.valueOf(emailToChange.getCampaign().getId());
        emailToChange.setTitle(emailModel.getTitle());
        emailToChange.setMessageBody(emailModel.getMessageBody());
        emailToChange.setSendDate(emailModel.getSendDate());
        emailToChange.setSendTime(emailModel.getSendTime());
        emailToChange.setStatus(EmailStatus.W_TRAKCIE);
        emailRepository.save(emailToChange);
        model.addAttribute("loggedUser", usePrincipal);
        return "redirect:/kampania/" + campaingId;
    }


        // Usuń konkretnego emaila z kampanii
    @PostMapping("/kampania/usun/email/{id}")
    public String postMethodName(@PathVariable String id,@RequestParam(name="campaingId") String campaingId,HttpServletRequest request,Model model) {
        Principal userPrincipal = request.getUserPrincipal();
        CampaignModel campaingEmailsToRemove = campaingService.findOneById(campaingId).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
        if(userPrincipal.getName().equals(campaingEmailsToRemove.getAccount().getEmail())){
            // List<EmailModel> listOfEmailsFromCampaing = campaingEmailsToRemove.getEmails();
            // EmailModel emailToDelete = listOfEmailsFromCampaing.get(Integer.parseInt(id));
            // listOfEmailsFromCampaing.remove(emailToDelete.getId());
            EmailModel emailToDelete = emailRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("Nie znaleziono danego maila"));
            List<OutgoingBook> listOutgoingByEmail = outgoingBookService.getAllOutgoingBookWithCampaing(emailToDelete);
            for(OutgoingBook outgoingToDelete : listOutgoingByEmail){
                outgoingBookRepository.delete(outgoingToDelete);
            }
            emailRepository.delete(emailToDelete);
            campaingRepository.save(campaingEmailsToRemove);
            return "redirect:/kampanie";
        } else {
            return "unauthorized";
        }
    }
}
