package com.example.fishmail.Controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Models.AccountRegistrationLinks;
import com.example.fishmail.Models.AdminEmail;
import com.example.fishmail.Models.Enum.CampaingStatus;
import com.example.fishmail.Repository.AccountRegistrationLinksRepository;
import com.example.fishmail.Repository.AccountRepository;
import com.example.fishmail.Repository.CampaingRepository;
import com.example.fishmail.Service.AccountRegistrationLinkService;
import com.example.fishmail.Service.AccountService;
import com.example.fishmail.Service.Admin.AdminEmailService;
import com.example.fishmail.Service.Admin.AdminService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    @Autowired
    private CampaingRepository campaingRepository;

    @Autowired
    private AdminEmailService adminEmailService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountRegistrationLinkService accountRegistrationLinkService;

    @Autowired
    private AccountRegistrationLinksRepository accountRegistrationLinksRepository;


    // Wyświetl dashboard admina
    @GetMapping("/admin/fishmail")
    public String getAdminDashboard(HttpServletRequest request,Model model) {
        Principal adminPrincipal = request.getUserPrincipal();
        AccountModel adminAccount = adminService.findOneByAccountAuthority("ROLE_ADMIN");
        if(adminPrincipal.getName().equals(adminAccount.getEmail())){
        List<AccountModel> userAccounts = adminService.getAllAccounts();

        model.addAttribute("userProfiles", userAccounts);
        model.addAttribute("loggedUser", adminPrincipal);
                    return "admin-dashboard";
        } else {
            return "unauthorized";
        }


    }

    // Wyświetl podstronę do utworzenia konta
    @GetMapping("/admin/fishmail/nowe-konto")
    public String getAdminCreateAccountPage(HttpServletRequest request,Model model) {
        Principal adminPrincipal = request.getUserPrincipal();
        AccountModel adminAccount = adminService.findOneByAccountAuthority("ROLE_ADMIN");
        if(adminPrincipal.getName().equals(adminAccount.getEmail())){
            AccountModel accountToCreate = new AccountModel();

            model.addAttribute("newAccount", accountToCreate);
            model.addAttribute("loggedUser", adminPrincipal);
            return "admin-create-account";

        } else {
            return "unauthorized";
        }
    }
    // Procesowanie założenia nowego konta
    @PostMapping("/admin/fishmail/stworz-konto")
    public String adminCreateUserProfileAccount(@ModelAttribute AccountModel accountToCreate, HttpServletRequest request, Model model) {
        Principal adminPrincipal = request.getUserPrincipal();
        AccountModel adminAccount = adminService.findOneByAccountAuthority("ROLE_ADMIN");
        if(adminPrincipal.getName().equals(adminAccount.getEmail())){
            adminService.createAccount(accountToCreate);
            return "redirect:/admin/fishmail";
        } else {
            return "unauthorized";
        }
    }
    
    
    // Wyświetl konkretne konto
    @GetMapping("/admin/fishmail/konto/{id}")
    public String getAdminUserProfile(@PathVariable String id,HttpServletRequest request, Model model) {
        Principal adminPrincipal = request.getUserPrincipal();
        AccountModel adminAccount = adminService.findOneByAccountAuthority("ROLE_ADMIN");
        if(adminPrincipal.getName().equals(adminAccount.getEmail())){
            AccountModel userProfile = adminService.findOneById(id);
            long campaingCountInProgress = campaingRepository.countByAccountAndStatus(userProfile, CampaingStatus.W_TRAKCIE);
            long campaingCountDone = campaingRepository.countByAccountAndStatus(userProfile, CampaingStatus.ZAKOŃCZONA);
            model.addAttribute("accountPrevCampaingInProgess", campaingCountInProgress);
            model.addAttribute("redeemUrl", "/admin/fishmail/konto-przedluz/" + userProfile.getId());
            model.addAttribute("accountPrevCampaingDone", campaingCountDone);
            model.addAttribute("userProfile", userProfile);
            model.addAttribute("loggedUser", adminPrincipal);
                    return "admin-user-profile";
        } else {
            return "unauthorized";
        }

    }

    // Procesuj przedłużenie ważności konta
    @PostMapping("/admin/fishmail/konto-przedluz/{id}")
    public String redeemAccountValidityTime(@PathVariable String id, HttpServletRequest request, Model model) {
        Principal adminPrincipal = request.getUserPrincipal();
        AccountModel adminAccount = adminService.findOneByAccountAuthority("ROLE_ADMIN");
        if(adminPrincipal.getName().equals(adminAccount.getEmail())){
            AccountModel userProfile = adminService.findOneById(id);
            System.out.println(userProfile.getEmail());
            adminService.redeemUserAccountValidity(userProfile.getEmail());
            return "redirect:/admin/fishmail/konto/" + id;
        } else {
            return "unauthorized";
        }
    }

    // Procesuj zablokowanie konta użytkownika
    @PostMapping("/admin/fishmail/konto-zablokuj/{id}")
    public String blockAccountByAdmin(@PathVariable String id,HttpServletRequest request, Model model) {
        Principal adminPrincipal = request.getUserPrincipal();
        AccountModel adminAccount = adminService.findOneByAccountAuthority("ROLE_ADMIN");
        if(adminPrincipal.getName().equals(adminAccount.getEmail())){
            AccountModel userProfile = adminService.findOneById(id);
            adminService.blockUserAccount(userProfile.getEmail());
            return "redirect:/admin/fishmail/konto/" + id;
        } else {
            return "unauthorized";
        }
    }
    
    // Procesuj usunięcie konta użytkownika
    @PostMapping("/admin/fishmail/konto-usun/{id}")
    public String postMethodName(@PathVariable String id,HttpServletRequest request, Model model) {
        Principal adminPrincipal = request.getUserPrincipal();
        AccountModel adminAccount = adminService.findOneByAccountAuthority("ROLE_ADMIN");
        if(adminPrincipal.getName().equals(adminAccount.getEmail())){
            String userProfile = adminService.findOneById(id).getEmail();
            adminService.adminDeleteAccount(userProfile);
            return "redirect:/admin/fishmail";
        } else {
            return "unauthorized";
        }
    }
    
    
    
    // Wyświetl formularz do edycji konta
    @GetMapping("/admin/fishmail/edytuj-konto/{id}")
    public String getAdminUserProfileEditPage(@PathVariable String id,HttpServletRequest request,Model model) {
        Principal adminPrincipal = request.getUserPrincipal();
        AccountModel adminAccount = adminService.findOneByAccountAuthority("ROLE_ADMIN");
        if(adminPrincipal.getName().equals(adminAccount.getEmail())){
            AccountModel accountToEdit = adminService.findOneById(id);

            model.addAttribute("accountToEdit", accountToEdit);
            model.addAttribute("loggedUser", adminPrincipal);
            return "admin-user-edit";
        } else {
            return "unauthorized";
        }
    }

    @PostMapping("/admin/fishmail/edytuj-uzytkownika/{id}")
    public String processAdminAccountChanges(@PathVariable String id,@RequestParam(name="extendValid",required = false) String extendValid,@RequestParam(name="toBlocked", required = false) String toBlocked, HttpServletRequest request,Model model){
        Principal adminPrincipal = request.getUserPrincipal();
        AccountModel adminAccount = adminService.findOneByAccountAuthority("ROLE_ADMIN");
        if(adminPrincipal.getName().equals(adminAccount.getEmail())){
            AccountModel userAccountToEdit = adminService.findOneById(id);
            LocalDateTime now = LocalDateTime.now();
            if(extendValid.equals("TAK")){
            userAccountToEdit.setValidAccountTime(now.plusDays(30));
            accountRepository.save(userAccountToEdit);
            }
            if(toBlocked.equals("TAK")){
                userAccountToEdit.setBlocked(true);
                accountRepository.save(userAccountToEdit);
            }
            return "redirect:/admin/fishmail";
        } else {
            return "unauthorized";
        }
        
        
    }
    

    // // Procesowanie usunięcia konkretnego konta
    // @GetMapping("/admin/fishmail/usun-konto/{id}")
    // public String deleteAdminUserProfile(@PathVariable String id, HttpServletRequest request,Model model) {
    //     Principal adminPrincipal = request.getUserPrincipal();
    //     AccountModel adminAccount = adminService.findOneByAccountAuthority("ROLE_ADMIN");
    //     if(adminPrincipal.getName().equals(adminAccount.getEmail())){
    //         AccountModel accountToDelete = adminService.findOneById(id);
    //         adminService.deleteAccount(accountToDelete);

    //         model.addAttribute("loggedUser", adminPrincipal);
    //         return "redirect:/admin/fishmail";
    //     } else {
    //         return "unauthorized";
    //     }
    // }
    
    
    // Wyświetl stronę do emailowania do wszystkich użytkowników fishmail
    @GetMapping("/admin/fishmail/uzytkownicy-wyslij-wiadomosc")
    public String getAdminSendEmailsToFishmailUsers(HttpServletRequest request, Model model) {
        Principal adminPrincipal = request.getUserPrincipal();
        AccountModel adminAccount = adminService.findOneByAccountAuthority("ROLE_ADMIN");
        if(adminPrincipal.getName().equals(adminAccount.getEmail())){
            AdminEmail adminEmail = new AdminEmail();

            model.addAttribute("adminEmail", adminEmail);
            model.addAttribute("loggedUser", adminPrincipal);
            return "admin-send-email-to-users";
        } else {
            return "unauthorized";
        }
    }

    // Wyślij wiadomość do użytkowników serwisu!
    @PostMapping("/admin/fishmail/wyslij-wiadomosc-do-uzytkownikow")
    public String sendMessageToAllUsersOfFishMail(@ModelAttribute AdminEmail adminEmail, HttpServletRequest request, Model model) {
        Principal adminPrincipal = request.getUserPrincipal();
        AccountModel adminAccount = adminService.findOneByAccountAuthority("ROLE_ADMIN");
        if(adminPrincipal.getName().equals(adminAccount.getEmail())){
            adminEmailService.sendMessagesToAllFishmailUsers(adminEmail);

            return "redirect:/admin/fishmail";
        } else {
            return "unauthorized";
        }
    }
    
    


    // Wyświetl stronę aktywacyjną konto
    @GetMapping("/aktywuj-konto")
    public String getAdminUserProfileActivate(@RequestParam(name="activationId") String activationId, HttpServletRequest request, Model model) {
        model.addAttribute("loggedUser", request.getUserPrincipal());
        model.addAttribute("activationId", activationId);
        return "user-profile-activate";
    }

    // Procesuj aktywacje konta
    @PostMapping("/aktywuj/{activationId}")
    public String processActivationService(@PathVariable String activationId) {
        
             adminService.activateAccount(activationId);
 


           return "redirect:/aktywuj-konto/ustaw-haslo?activationId=" + activationId;
    }
    

    // Wyświetl stronę dla wygasłego linku aktywacyjnego
    @GetMapping("/link-aktywacyjny-wygasl")
    public String getActivationLinkDeadPage(HttpServletRequest request, Model model) {
        Principal userPrincipal = request.getUserPrincipal();

        model.addAttribute("loggedUser", userPrincipal);
        
        return "user-profile-activate-dead";
    }
    
    
    
   // Wyświetl stronę która po aktywacji konta przyjmie hasło podane przez użytkownika
    @GetMapping("/aktywuj-konto/ustaw-haslo")
    public String getAdminUserProfilePassword(@RequestParam(name = "activationId") String activationId,HttpServletRequest request, Model model) {
        model.addAttribute("activationId", activationId);
        model.addAttribute("loggedUser", request.getUserPrincipal());
        return "user-profile-password-change";
    }

    // Procesuj założenie pierwszego hasła
    @PostMapping("/aktywuj-konto/zapisz-haslo/{activationId}")
    public String processUserPasswordSet(@PathVariable String activationId,@RequestParam(name="password") String password) {
        adminService.setUserPassword(activationId, password);
        
        return "redirect:/zaloguj";
    }
    

    // Wyświetl formularz do ponownego wysłania linku aktywacyjnego
    @GetMapping("/wyslij-link-aktywacyjny")
    public String getAdminUserResendActivationLink(HttpServletRequest request, Model model) {
        Principal userPrincipal = request.getUserPrincipal();
        model.addAttribute("loggedUser", userPrincipal);
        return "user-profile-resend-link";
    }

    @PostMapping("/link-aktywacyjny")
    public String processResendUserActivationLink(@RequestParam(name = "email") String email) {
        adminService.reSendActivationLink(email);
        
        return "redirect:/zaloguj";
    }
    
    
    

}
