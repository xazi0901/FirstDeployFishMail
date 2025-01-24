package com.example.fishmail.Controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.query.SortDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import com.example.fishmail.Repository.CampaingRepository;
import com.example.fishmail.Repository.EmailRepository;
import com.example.fishmail.Repository.OutgoingBookRepository;
import com.example.fishmail.Service.AccountService;
import com.example.fishmail.Service.CampaingService;
import com.example.fishmail.Service.OutgoingBookService;
import com.example.fishmail.Service.RecieverService;
import com.example.fishmail.Service.Admin.AdminService;
import com.example.fishmail.Service.DynamicDataService.DynamicDataService;
import com.example.fishmail.Service.Excel.ExcelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    @Autowired
    private CampaingRepository campaingRepository;

    @GetMapping("/kampanie")
    public String getCampaingHome(HttpServletRequest request,Model model) {
        Principal userPrincipal = request.getUserPrincipal();
        AccountModel accountModel = accountService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new RuntimeException("Nie znaleziono konta!"));

        if(accountModel.isFirstLogin()){
            return "redirect:/profil/edytuj";
          
        }

        List<CampaignModel> campaingList = accountModel.getCampaing();
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
            double outgoingInEndEffectivness = totalOutgoingBooks > 0 ? (outgoingBookStatusWyslano * 100 / totalOutgoingBooks) : 0.0;
            double outgoingOpenedEffectivness = totalOutgoingBooks > 0 ? (outgoingOpenedCount * 100 / totalOutgoingBooks) : 0.0;

            stats.put("outgoingInEnd", outgoingBookStatusWyslano);
            stats.put("outgoingOpened", outgoingOpenedCount);
            stats.put("outgoingBookTotal", effectiveness);
            stats.put("outgoingInEndEffectivness",outgoingInEndEffectivness);
            stats.put("outgoingOpenedEffectivness",outgoingOpenedEffectivness);

            emailStatistics.put(emailOutgoing, stats);
        }


            model.addAttribute("loggedUser", userPrincipal.getName());
            model.addAttribute("campaingToShow", campaignToShow);
            
            model.addAttribute("totalEmailCampaings", totalSizeOfEmail);
            model.addAttribute("campaingEmailToShow", emailToShow);
            model.addAttribute("campaingInProgress", countEmailByStatusWTrakcie);
            model.addAttribute("campaingEnd", countEmailByStatusZakonczone);
            model.addAttribute("emailStatistics", emailStatistics);

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
    public String postMethodName(@PathVariable String id,@ModelAttribute CampaignModel campaingToShow, HttpServletRequest request,Model model) {
        Principal userPrincipal = request.getUserPrincipal();
        CampaignModel campaingToEdit = campaingService.findOneById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
        if(userPrincipal.getName().equals(campaingToEdit.getAccount().getEmail())){
            campaingToEdit.setEmails(campaingToShow.getEmails());
            campaingToEdit.setName(campaingToShow.getName());
            campaingToEdit.setRecivers(campaingToShow.getRecivers());
            return "redirect:/kampanie";
        } else {
            return "unauthorized";
        }
    }
    

    // Wyświetl książkę wychodzącą dla konkretnego emaila
    // @GetMapping("/kampania/ksiega-korespondencji/{id}")
    // public String getUserCampaingOutgoingBook(@PathVariable String id,HttpServletRequest request,Model model) {
    //     List<OutgoingBook> outgoingBookList = outgoingBookService.getAllOutgoingBooksForEmailCampaing(id);
    //     Principal userPrincipal = request.getUserPrincipal();
    //     model.addAttribute("loggedUser", userPrincipal);
    //     model.addAttribute("outgoingBooks", outgoingBookList);
    //     return "campaing-email";
    // }
     @GetMapping("/kampania/ksiega-korespondencji/{id}")
    public String getOutgoingBookByStatus(@PathVariable String id,@RequestParam(required = false,name = "status") String status, HttpServletRequest request, Model model) {
        Principal userPrincipal = request.getUserPrincipal();
        List<OutgoingBook> outgoingBooks;

        if (status != null && !status.isEmpty()) {
            outgoingBooks = outgoingBookRepository.findByStatusAndEmailId(SendingStatus.valueOf(status),Long.valueOf(id));
        } else {
         outgoingBooks = outgoingBookService.getAllOutgoingBookWithCampaing(emailRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("Nie znaleziono wiadomości")));
        }
    //    List<OutgoingBook> outgoingBooks = outgoingBookService.getAllOutgoingBooksForEmailCampaing(id);

       Map<String,Double> sendedData = new LinkedHashMap<>();
       Map<String,Double> openedData = new LinkedHashMap<>();
       Map<String,Double> errorData = new LinkedHashMap<>();
       Map<String,Double> plannedData = new LinkedHashMap<>();
       Map<String,Double> totalStatistics = Map.of();
       for(OutgoingBook emailForStatistics : outgoingBooks){
            EmailModel emailToGet = emailForStatistics.getEmail();
            int zaplanowanoMessages = (int)outgoingBookRepository.countByEmailAndStatus(emailToGet, SendingStatus.ZAPLANOWANA);
            int wTrakcieMessages = (int)outgoingBookRepository.countByEmailAndStatus(emailToGet,SendingStatus.W_TRAKCIE);
            int wyslanoMessages = (int)outgoingBookRepository.countByEmailAndStatus(emailToGet, SendingStatus.WYSŁANO);
            int bladMessages = (int)outgoingBookRepository.countByEmailAndStatus(emailToGet, SendingStatus.BŁĄD);
            int openedMessages = outgoingBookRepository.countOutgoingBooksByIsOpened(true).size();
            int totalMessages = zaplanowanoMessages + wTrakcieMessages+wyslanoMessages+bladMessages+openedMessages;
            sendedData.put("Suma Procentowa Wysłanych Wiadomości", (wyslanoMessages / (double) totalMessages) * 100);
            sendedData.put("Suma wiadomości",(wyslanoMessages - (double) totalMessages) *100);
            // sendedData = Map.of(
            //     "Suma Procentowa Wysłanych Wiadomości",(wyslanoMessages / (double) totalMessages) * 100,
            //     "Suma wiadomości",(wyslanoMessages - (double) totalMessages) *100
            // );
            openedData.put("Suma Procentowa Otwartych Wiadomości", (openedMessages / (double) totalMessages) * 100);
            openedData.put("Suma wiadomości",(openedMessages - (double) totalMessages) * 100);
            // openedData = Map.of(
            //     "Suma Procentowa Otwartych Wiadomości", (openedMessages / (double) totalMessages) * 100,
            //     "Suma wiadomości",(openedMessages - (double) totalMessages) * 100
            // );
            errorData.put("Suma Procentowa Błędów Wysłania Wiadomości", (bladMessages / (double) totalMessages) * 100);
            errorData.put("Suma wiadomości",(bladMessages - (double) totalMessages) * 100);
            // errorData = Map.of(
            //     "Suma Procentowa Błędów Wysłania Wiadomości", (bladMessages / (double) totalMessages) * 100,
            //     "Suma wiadomości",(bladMessages - (double) totalMessages) * 100
            // );
            plannedData.put("Suma Procentowa Zaplanowanych Wiadomości", (zaplanowanoMessages / (double) totalMessages) *100);
            plannedData.put("Suma wiadomości",(zaplanowanoMessages - (double) totalMessages) * 100);
            // plannedData = Map.of(
            //     "Suma Procentowa Zaplanowanych Wiadomości", (zaplanowanoMessages / (double) totalMessages) *100,
            //     "Suma wiadomości",(zaplanowanoMessages - (double) totalMessages) * 100
            // );
            totalStatistics = Map.of(
    "Procent Sumy Wiadomości zaplanowanych", (zaplanowanoMessages / (double) totalMessages) * 100,
    "Procent Sumy Wiadomości wysłanych", (wyslanoMessages / (double) totalMessages) * 100,
    "Procent Sumy Wiadomości z błędem", (bladMessages / (double) totalMessages) * 100,
    "Procent Sumy Wiadomości otwartych", (openedMessages / (double) totalMessages) * 100
        );

          model.addAttribute("totalSended", wyslanoMessages);
          model.addAttribute("totalOpened", openedMessages);
          model.addAttribute("totalError", bladMessages);
          model.addAttribute("totalPlanned", zaplanowanoMessages);
          model.addAttribute("totalMessages", totalMessages);

       }
        // if (status != null && !status.isEmpty()) {
        //     outgoingBooks = outgoingBookRepository.findByStatus(SendingStatus.valueOf(status));
        // } else {
        //     outgoingBooks;
        // }

        List<String> sendedLabels = new ArrayList<>(sendedData.keySet());
        List<Double> sendedValues = new ArrayList<>(sendedData.values());

        List<String> openedLabels = new ArrayList<>(openedData.keySet());
        List<Double> openedValues = new ArrayList<>(openedData.values());

        List<String> errorLabels = new ArrayList<>(errorData.keySet());
        List<Double> errorValues = new ArrayList<>(errorData.values());

        List<String> plannedLabels = new ArrayList<>(plannedData.keySet());
        List<Double> plannedValues = new ArrayList<>(plannedData.values());
                // Konwertuj dane na JSON
        // String labelsJson = objectMapper.writeValueAsString(data.keySet());
       // Konwertuj keySet() i values() na listy
        List<String> labelsJson = new ArrayList<>(totalStatistics.keySet());
        List<Double> valuesJson = new ArrayList<>(totalStatistics.values());
        // String valuesJson = objectMapper.writeValueAsString(data.values());
        model.addAttribute("sendedLabels", sendedLabels);
        model.addAttribute("sendedValues", sendedValues);

        model.addAttribute("openedLabels", openedLabels);
        model.addAttribute("openedValues", openedValues);

        model.addAttribute("errorLabels", errorLabels);
        model.addAttribute("errorValues", errorValues);

        model.addAttribute("plannedLabels", plannedLabels);
        model.addAttribute("plannedValues", plannedValues);


        model.addAttribute("labelsJson", labelsJson);
        model.addAttribute("valuesJson", valuesJson);

      
     // Dodaj dane do modelu
        // model.addAttribute("chartData", data);
        model.addAttribute("outgoingBooks", outgoingBooks);
        model.addAttribute("loggedUser", userPrincipal);
        model.addAttribute("emailId", id);
        return "campaing-email";
    }
    
    
    // Wyświetl podstronę z odbiorcami oraz możliwością ich zamiany
    @GetMapping("/kampania/odbiorcy/{id}")
    public String getRecieversListByCampaing(@PathVariable String id,HttpServletRequest request,Model model) {
        Principal userPrincipal = request.getUserPrincipal();
        CampaignModel findCampaing = campaingService.findOneById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
        List<RecieversModel> recieversList = recieverService.getRecieversListFromCampaing(findCampaing);
        RecieversModel recieversModel = new RecieversModel();
        model.addAttribute("campaingId", id);
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

}
