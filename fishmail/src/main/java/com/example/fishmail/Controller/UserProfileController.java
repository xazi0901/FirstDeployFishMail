package com.example.fishmail.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.Enum.CampaingStatus;
import com.example.fishmail.Repository.CampaingRepository;
import com.example.fishmail.Service.AccountService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class UserProfileController {
    
    @Autowired
    private AccountService accountService;
    @Autowired
    private CampaingRepository campaingRepository;

    // Wyświetl profil
    @GetMapping("/profil")
    @PreAuthorize("isAuthenticated()")
    public String getUserProfile(HttpServletRequest request,Model model) {
        Principal loggedUser = request.getUserPrincipal();
        Optional<AccountModel> optionalAccount = accountService.findByEmail(loggedUser.getName());

        if (optionalAccount.isPresent()) {
            AccountModel accountPreview = optionalAccount.get();
            List<CampaignModel> listOfCampaing= accountPreview.getCampaing();
            // Do dodania liczniki według statusu

            if (loggedUser.getName().equals(accountPreview.getEmail())) {
                model.addAttribute("accountPrev", accountPreview);
                model.addAttribute("loggedUser", loggedUser);
                                long campaingCountInProgress = campaingRepository.countByAccountAndStatus(accountPreview, CampaingStatus.W_TRAKCIE);
                long campaingCountDone = campaingRepository.countByAccountAndStatus(accountPreview, CampaingStatus.ZAKOŃCZONA);
                model.addAttribute("accountPrevCampaingInProgess", campaingCountInProgress);
                model.addAttribute("accountPrevCampaingDone", campaingCountDone);
            //     for(CampaignModel campaingsStatus : listOfCampaing){

            // }

                return "profile";
            } else {
                return "unauthorized";
            }

        } else {
            return "404";
        }
    }


    @GetMapping("/profil/edytuj")
    public String getUserFormEditProfile(HttpServletRequest request, Model model) {
       Principal loggedUser = request.getUserPrincipal();
        Optional<AccountModel> optionalAccount = accountService.findByEmail(loggedUser.getName());

        if (optionalAccount.isPresent()) {
            AccountModel accountPreview = optionalAccount.get();
            List<CampaignModel> listOfCampaing= accountPreview.getCampaing();
            // Do dodania liczniki według statusu

            if (loggedUser.getName().equals(accountPreview.getEmail())) {
                // model.addAttribute("accountPrev", accountPreview);
                model.addAttribute("loggedUser", loggedUser);
                model.addAttribute("accountToEdit", accountPreview);
            //     for(CampaignModel campaingsStatus : listOfCampaing){

            // }

                return "edit-profile";
            } else {
                return "unauthorized";
            } 
        } else {
            return "404";
        }
    }
    

    // Procesuj zmiane profilu
    @PostMapping("/edytuj-profil/")
    public String changeUserProfile(@ModelAttribute AccountModel account,HttpServletRequest request,Model model) {
        Principal userPrincipal = request.getUserPrincipal();
        AccountModel userProfile = accountService.findByEmail(userPrincipal.getName()).orElseThrow(() -> new RuntimeException("Nie znaleziono konta!"));

        if(userPrincipal.getName().equals(userProfile.getEmail())){
            userProfile.setSmtpHost(account.getSmtpHost());
            userProfile.setSmtpLogin(account.getSmtpLogin());
            userProfile.setSmtpPassword(account.getSmtpPassword());
            userProfile.setSmtpPort(account.getSmtpPort());
            userProfile.setFirstLogin(false);
        
            accountService.save(userProfile);
            model.addAttribute("loggedUser", userPrincipal);
            return "redirect:/profil";
        } else {
            return "unauthorized";
        }
    }
    
    
}
