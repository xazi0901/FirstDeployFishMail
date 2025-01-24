package com.example.fishmail.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Models.OutgoingBook;
import com.example.fishmail.Models.RecieversModel;
import com.example.fishmail.Models.Enum.SendingStatus;
import com.example.fishmail.Repository.CampaingRepository;
import com.example.fishmail.Repository.OutgoingBookRepository;
import com.example.fishmail.Repository.RecieversRepository;
import com.example.fishmail.Service.CampaingService;
import com.example.fishmail.Service.OutgoingBookService;
import com.example.fishmail.Service.RecieverService;
import com.example.fishmail.Service.Excel.ExcelService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class RecieversController {
    
    @Autowired
    private CampaingService campaingService;

    @Autowired
    private CampaingRepository campaingRepository;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private RecieverService recieverService;

    @Autowired
    private RecieversRepository recieversRepository;

    @Autowired
    private OutgoingBookService outgoingBookService;

    @Autowired
    private OutgoingBookRepository outgoingBookRepository;

    // Usuń pojedyńczego odbiorcę z listy danej kampanii
    @PostMapping("/kampania/usun-odbiorce/{id}")
    public String processRemoveRecieversFromCampaing(@PathVariable String id,@RequestParam(name="campaingId") String campaingId, HttpServletRequest request, Model model) {
       CampaignModel campaignModelToEdit = campaingService.findOneById(campaingId).orElseThrow(() -> new RuntimeException("nie znaleziono kampanii"));
       List<RecieversModel> recieversToEdit = campaignModelToEdit.getRecivers();
        recieversToEdit.removeIf(reciever ->reciever.getId() == Long.parseLong(id));
        // Zaktualizuj listę odbiorców w kampanii
        campaignModelToEdit.setRecivers(recieversToEdit);
        // Zapisz zmiany w bazie danych
        campaingRepository.save(campaignModelToEdit);
        return "redirect:/kampania/odbiorcy/" + campaingId;
       
    }

    // Edytuj pojedyńczego odbiorcę z listy danej kampanii
    @PostMapping("/kampania/edytuj-odbiorce/{id}")
    public String processEditRecieversFromCampaing(@PathVariable String id,@RequestParam(name="campaingId") String campaingId,@RequestParam(name = "recieverEmail") String recieverEmail, HttpServletRequest request,Model model) {
        CampaignModel campaignModelToEdit = campaingService.findOneById(campaingId).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
        List<RecieversModel> recieversToEdit = campaignModelToEdit.getRecivers();
        for(RecieversModel reciverToEdit : recieversToEdit){
            if(reciverToEdit.getId() == Long.parseLong(id)){
                // OutgoingBook outgoingBook = outgoingBookRepository.f
                List<OutgoingBook> outgoingBookReciever = outgoingBookRepository.findAllByRecivierEmail(reciverToEdit.getRecieverEmail());
                    reciverToEdit.setRecieverEmail(recieverEmail);
                for(OutgoingBook outgoingSingle : outgoingBookReciever){
                outgoingSingle.setRecivierEmail(recieverEmail);
                outgoingBookRepository.save(outgoingSingle);
                }
            

                
            }
        }
        campaignModelToEdit.setRecivers(recieversToEdit);
        campaingRepository.save(campaignModelToEdit);
        return "redirect:/kampania/odbiorcy/" + campaingId;        
        
    }

    // Usuń kilku zaznaczonych odbiorców na liście
    @PostMapping("/kampania/akcje-masowe/{id}")
    public String processRemoveListOfRecievers(@PathVariable String id,@RequestParam(name ="actions") String actions,@RequestParam(name ="recieverId") List<String> recieversId, HttpServletRequest request, Model model) {
        CampaignModel campaignModelToEdit = campaingService.findOneById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
        List<RecieversModel> recieverModelToDelete = campaignModelToEdit.getRecivers();
        switch(actions){
            case "delete":
                for(RecieversModel reciverToDelete : recieverModelToDelete){
                    for(String recieverIdToDelete : recieversId){
                        if(String.valueOf(reciverToDelete.getId()).equals(recieverIdToDelete)){
                            recieverModelToDelete.remove(reciverToDelete);
                        }
                    }
                }
                campaignModelToEdit.setRecivers(recieverModelToDelete);
                campaingRepository.save(campaignModelToEdit);
                break;

            default: 
                model.addAttribute("message", "Nie wybrano żądnej akcji");
        }
            return "redirect:/kampania/odbiorcy/" + id;
    }
    

    @PostMapping("/kampania/zamien-odbiorcow/{id}")
    public String processChangingListOfRecievers(@PathVariable String id,@RequestParam("recipientsFile") MultipartFile file,@RequestParam("recipientsInput") String recipientsInput,Model model,HttpServletRequest request) throws IOException {
        CampaignModel campaignModelToEdit = campaingService.findOneById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
        List<EmailModel> emailsFromCampaing = campaignModelToEdit.getEmails();


        for(EmailModel em : emailsFromCampaing){
            List<OutgoingBook> oldOutgoingBooks = outgoingBookService.getAllOutgoingBookWithCampaing(em);
            outgoingBookRepository.deleteAll(oldOutgoingBooks);
        }

        //Zmiany odbiorców
        List<String> recieversFile = new ArrayList<>();
        // Odczyt pliku odbiorców
        if(!file.isEmpty()){
            recieversFile = excelService.readRecieviersFromFile(file);
        }

        // Odczyt odbiorców z pola tekstowego
        List<String> recieversInput = Arrays.asList(recipientsInput.split(","));

        //Połącz odbiorców z pliku i pola
        List<String> allRecievers = new ArrayList<>();
        allRecievers.addAll(recieversFile);
        allRecievers.addAll(recieversInput);
        List<RecieversModel> newRecievers = allRecievers.stream().map(email -> new RecieversModel(email,campaignModelToEdit)).collect(Collectors.toList());
        campaignModelToEdit.getRecivers().clear();
        campaignModelToEdit.getRecivers().addAll(newRecievers);
        campaingRepository.save(campaignModelToEdit);
        for(RecieversModel newReciever : newRecievers){
            
            for(EmailModel emailFromCampaing : emailsFromCampaing){
                
                 OutgoingBook outgoingBookToSave = new OutgoingBook();
                   outgoingBookToSave.setStatus(SendingStatus.ZAPLANOWANA);
                outgoingBookToSave.setEmail(emailFromCampaing);
                outgoingBookToSave.setRecivierEmail(newReciever.getRecieverEmail());
                outgoingBookToSave.setTrackingId(UUID.randomUUID().toString());
                outgoingBookService.getAllOutgoingBookWithCampaing(emailFromCampaing).add(outgoingBookToSave);
                                    outgoingBookToSave.setSendTime(emailFromCampaing.getSendTime());
                    outgoingBookToSave.setSendDate(emailFromCampaing.getSendDate());

                
                    outgoingBookRepository.save(outgoingBookToSave);
            }
        }
        
        return "redirect:/kampania/odbiorcy/" +id;
    }
    
    
    
}
