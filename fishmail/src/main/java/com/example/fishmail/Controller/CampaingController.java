package com.example.fishmail.Controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Models.OutgoingBook;
import com.example.fishmail.Models.RecieversModel;
import com.example.fishmail.Models.Enum.EmailStatus;
import com.example.fishmail.Models.Enum.SendingStatus;
import com.example.fishmail.Repository.EmailRepository;
import com.example.fishmail.Repository.OutgoingBookRepository;
import com.example.fishmail.Service.AccountService;
import com.example.fishmail.Service.CampaingService;
import com.example.fishmail.Service.OutgoingBookService;
import com.example.fishmail.Service.RecieverService;
import com.example.fishmail.Service.Admin.AdminService;
import com.example.fishmail.Service.DynamicDataService.DynamicDataService;
import com.example.fishmail.Service.Excel.ExcelService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class CampaingController {
    
    @Autowired
    private CampaingService campaingService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private OutgoingBookService outgoingBookService;

    @Autowired
    private DynamicDataService dynamicDataService;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private OutgoingBookRepository outgoingBookRepository;

    @Autowired
    private RecieverService recieverService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/kampanie")
    public String getCampaingHome(HttpServletRequest request,Model model) {
        Principal userPrincipal = request.getUserPrincipal();
        AccountModel accountModel = accountService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new RuntimeException("Nie znaleziono konta!"));

        if(accountModel.isFirstLogin()){
            return "redirect:/profil/edytuj";
          
        }

        List<CampaignModel> campaingList = campaingService.getAllCampaings();
          // Mapowanie kampanii na dane tabeli
        List<Map<String, Object>> tableData = dynamicDataService.mapEntitiesToTable(campaingList);

        // Dodanie danych do widoku
        model.addAttribute("columns", tableData.isEmpty() ? List.of() : tableData.get(0).keySet());
        model.addAttribute("rows", tableData);
        model.addAttribute("campaings", campaingList);
          model.addAttribute("loggedUser", userPrincipal);
        return "campaing";
    }

    @GetMapping("/nowa-kampania")
    public String getNewCampaing(Model model, HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        CampaignModel newCampaing = new CampaignModel();
        model.addAttribute("loggedUser", userPrincipal.getName());
        model.addAttribute("campaingModel", newCampaing);
        return "new-campaing";
    }

    @PostMapping("/zapisz-kampanie")
    public String postMethodName(@ModelAttribute CampaignModel campaing,@RequestParam("recipientsFile") MultipartFile file,@RequestParam("recipientsInput") String recipientsInput,Model model,HttpServletRequest request) {
        try{

        Principal userPrincipal = request.getUserPrincipal();
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

        List<RecieversModel> recievers = allRecievers.stream().map(email -> new RecieversModel(email,campaing)).collect(Collectors.toList());
        campaing.setRecivers(recievers);

        campaingService.createCampaing(campaing, userPrincipal.getName());      

        // outgoingBookService.saveOutgoingBookWithObject(outgoingBook,String.valueOf(campaing.getId()));
        outgoingBookService.saveOutgoingBookWithoutObject(String.valueOf(campaing.getId()));
        model.addAttribute("loggedUser", userPrincipal.getName());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/kampanie";
    }

    @GetMapping("/kampania/{id}")
    public String getUserCampaing(@PathVariable String id,HttpServletRequest request,Model model) {
        CampaignModel campaignToShow = campaingService.findOneById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii!"));
        Principal userPrincipal = request.getUserPrincipal();
        if(campaignToShow.getAccount().getEmail().equals(userPrincipal.getName())){
            List<EmailModel> emailToShow = campaignToShow.getEmails();
            int totalSizeOfEmail = emailToShow.size();
            long countEmailByStatusWTrakcie = emailRepository.countByCampaignAndStatus(campaignToShow, EmailStatus.W_TRAKCIE);
            long countEmailByStatusZakonczone = emailRepository.countByCampaignAndStatus(campaignToShow, EmailStatus.ZAKOŃCZONA);
            // List<OutgoingBook> outgoingBookList = outgoingBookService.getAllOutgoingBookWithCampaing(null)
            

        // Mapa na statystyki dla każdego e-maila
        Map<EmailModel, Map<String, Object>> emailStatistics = new HashMap<>();

        for (EmailModel emailOutgoing : emailToShow) {
            Map<String, Object> stats = new HashMap<>();
            long outgoingBookByStatusWTrakcie = outgoingBookRepository.countByEmailAndStatus(emailOutgoing, SendingStatus.W_TRAKCIE);
            long outgoingBookStatusWyslano = outgoingBookRepository.countByEmailAndStatus(emailOutgoing, SendingStatus.WYSŁANO);
            long outgoingOpenedCount = outgoingBookRepository.countByEmailAndIsOpenedTrue(emailOutgoing);

            int totalOutgoingBooks = outgoingBookRepository.findAllByEmail(emailOutgoing).size();
            double effectiveness = totalOutgoingBooks > 0 ? (outgoingOpenedCount * 100.0 / totalOutgoingBooks) : 0.0;

            stats.put("outgoingInEnd", outgoingBookStatusWyslano);
            stats.put("outgoingOpened", outgoingOpenedCount);
            stats.put("outgoingBookTotal", effectiveness);

            emailStatistics.put(emailOutgoing, stats);
        }


            model.addAttribute("loggedUser", userPrincipal.getName());
            model.addAttribute("campaingToShow", campaignToShow);
            
            model.addAttribute("totalEmailCampaings", totalSizeOfEmail);
            model.addAttribute("campaingEmailToShow", emailToShow);
            model.addAttribute("campaingInProgress", countEmailByStatusWTrakcie);
            model.addAttribute("campaingEnd", countEmailByStatusZakonczone);
            model.addAttribute("emailStatistics", emailStatistics);


            // for(EmailModel emailOutgoing : emailToShow){
            //     List<OutgoingBook> findAllByEmail = outgoingBookRepository.findAllByEmail(emailOutgoing);
            //     int totalSizeOfOutgoingBook = findAllByEmail.size();
            //     model.addAttribute("outgoingBookStatistics", findAllByEmail);
            //     model.addAttribute("sizeOutgoingBook", totalSizeOfOutgoingBook);
                
            // }



            // for(EmailModel emailOutgoing : emailToShow){
            //     long outgoingBookByStatusWTrakcie = outgoingBookRepository.countByEmailAndStatus(emailOutgoing, SendingStatus.W_TRAKCIE);
            //     long outgoingBookStatusWyslano = outgoingBookRepository.countByEmailAndStatus(emailOutgoing, SendingStatus.WYSŁANO);
            //     List<OutgoingBook> outgoingBookOpened = outgoingBookRepository.findAllByIsOpened(true);
            //     List<OutgoingBook> totalSizeOpeningBook = outgoingBookRepository.findAllByEmail(emailOutgoing);
            //     double percentageOfEffectivenes = outgoingBookOpened.size() * 100 / totalSizeOpeningBook.size();
            //     model.addAttribute("outgoingOpened", outgoingBookOpened);
            //     model.addAttribute("outgoingInProgress", outgoingBookByStatusWTrakcie);
            //     model.addAttribute("outgoingInEnd", outgoingBookStatusWyslano);
            //     model.addAttribute("outgoingBookTotal", percentageOfEffectivenes);
            // }
            // for(EmailModel emailForOutgoingBook : emailToShow){
            //     List<OutgoingBook> outgoingBooksByEmail = outgoingBookService.getAllOutgoingBookWithCampaing(emailForOutgoingBook);
            //     model.addAttribute("outgoingBookByEmail", outgoingBooksByEmail);
            // }
            

            

            return "user-campaing";
        } else {
            return "404";
        }
    }

    // Wyświetl formularz kampanii do edycji
    @GetMapping("/kampania/edytuj/{id}")
    public String getUserCampaingFormToChange(@PathVariable String id,HttpServletRequest request,Model model) {
        CampaignModel campaingToShow = campaingService.findOneById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono kampaniii"));
        Principal userPrincipal = request.getUserPrincipal();
        if(campaingToShow.getAccount().getEmail().equals(userPrincipal.getName())){
            model.addAttribute("campaingToShow", campaingToShow);
            model.addAttribute("loggedUser", userPrincipal);
            return "user-campaing-edit";
        } else {
            return "404";
        }
    }

    // Procesowanie edycji zmiany danych kampanii
    @PostMapping("/kampania/edytuj-kampanie/{id}")
    public String postMethodName(@PathVariable String id,@ModelAttribute CampaignModel campaingDataToEdit, HttpServletRequest request,Model model) {
        Principal userPrincipal = request.getUserPrincipal();
        CampaignModel campaingToEdit = campaingService.findOneById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
        if(userPrincipal.getName().equals(campaingToEdit.getAccount().getEmail())){
            campaingToEdit.setEmails(campaingDataToEdit.getEmails());
            campaingToEdit.setName(campaingDataToEdit.getName());
            campaingToEdit.setRecivers(campaingDataToEdit.getRecivers());
            return "redirect:/kampania";
        } else {
            return "unauthorized";
        }
    }
    

    // Wyświetl książkę wychodzącą dla konkretnego emaila
    @GetMapping("/kampania/email/{id}")
    public String getUserCampaingOutgoingBook(@PathVariable String id,HttpServletRequest request,Model model) {
        List<OutgoingBook> outgoingBookList = outgoingBookService.getAllOutgoingBooksForEmailCampaing(id);
        Principal userPrincipal = request.getUserPrincipal();
        model.addAttribute("loggedUser", userPrincipal);
        model.addAttribute("outgoingBooks", outgoingBookList);
        return "campaing-email";
    }
    
    // Wyświetl podstronę z odbiorcami oraz możliwością ich zamiany
    @GetMapping("/kampania/odbiorcy/{id}")
    public String getRecieversListByCampaing(@PathVariable String id,HttpServletRequest request,Model model) {
        Principal userPrincipal = request.getUserPrincipal();
        CampaignModel findCampaing = campaingService.findOneById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
        List<RecieversModel> recieversList = recieverService.getRecieversListFromCampaing(findCampaing);
        RecieversModel recieversModel = new RecieversModel();
        model.addAttribute("newRecieversModel", recieversModel);
        model.addAttribute("loggedUser", userPrincipal);
        model.addAttribute("recieversList", recieversList);
        return "user-campaing-recievers";
    }

    // Procesowanie edycji odbiorców kampanii
    @PostMapping("/kampania/edytuj-odbiorcow/{id}")
    public String changeRecieversInCampaing(@PathVariable String id,HttpServletRequest request, Model model,@RequestParam("recipientsFile") MultipartFile file,@RequestParam("recipientsInput") String recipientsInput) throws IOException {
        CampaignModel userCampaing = campaingService.findOneById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
        Principal userPrincipal = request.getUserPrincipal();
       if(userCampaing.getAccount().getEmail().equals(userPrincipal.getName())){
      
        try{

       
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

        List<RecieversModel> recievers = allRecievers.stream().map(email -> new RecieversModel(email,userCampaing)).collect(Collectors.toList());
        userCampaing.setRecivers(recievers);
        model.addAttribute("loggedUser", userPrincipal);
        } catch(Exception e){
            throw new RuntimeException(e);
       }
       return "redirect:/kampanie";

    } else {
        return "unathorized";
    }
    
}


    // Procesowanie usunięcia kampanii
    @PostMapping("/kampania/usun/{id}")
    public String postMethodName(@PathVariable String id,HttpServletRequest request,Model model) {
        Principal loggedUser = request.getUserPrincipal();
        CampaignModel campaignModel = campaingService.findOneById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii o podanym id"));
        if(loggedUser.getName().equals(campaignModel.getAccount().getEmail())){
            campaingService.deleteCampaingById(id);

            model.addAttribute("loggedUser", loggedUser);
            return "redirect:/kampanie";
        } else {
            return "unauthorized";
        }

    }


    // Dodaj emaile do kampanii
    @PostMapping("/kampania/dodaj-email/{id}")
    public String addEmailToCampaing(@PathVariable String campaingId,@ModelAttribute EmailModel emailModel, HttpServletRequest request,Model model) {
       Principal userPrincipal = request.getUserPrincipal();
       CampaignModel campaingToAdd = campaingService.findOneById(campaingId).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii!"));
       if(userPrincipal.getName().equals(campaingToAdd.getAccount().getEmail())){
       List<EmailModel> listOfEmailsToAdd = campaingToAdd.getEmails();
       listOfEmailsToAdd.add(emailModel);
          return "redirect:/kampanie";
       } else {
        return "unathorized";
       }
     
    }
    

    // Usuń emaile z kampanii
    @PostMapping("/kampania/usun/email/{id}")
    public String postMethodName(@PathVariable String id,@PathVariable String campaingId,HttpServletRequest request,Model model) {
        Principal userPrincipal = request.getUserPrincipal();
        CampaignModel campaingEmailsToRemove = campaingService.findOneById(campaingId).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
        if(userPrincipal.getName().equals(campaingEmailsToRemove.getAccount().getEmail())){
            List<EmailModel> listOfEmailsFromCampaing = campaingEmailsToRemove.getEmails();
            EmailModel emailToDelete = listOfEmailsFromCampaing.get(Integer.parseInt(id));
            listOfEmailsFromCampaing.remove(emailToDelete.getId());
            return "redirect:/kampanie";
        } else {
            return "unauthorized";
        }
    }
    
    
    
    
    
    
}
