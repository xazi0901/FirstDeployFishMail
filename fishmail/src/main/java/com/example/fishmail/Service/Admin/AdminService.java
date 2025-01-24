package com.example.fishmail.Service.Admin;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.management.RuntimeErrorException;
import javax.swing.text.html.parser.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Models.AccountRegistrationLinks;
import com.example.fishmail.Models.AuthorityModel;
import com.example.fishmail.Repository.AccountRegistrationLinksRepository;
import com.example.fishmail.Repository.AccountRepository;
import com.example.fishmail.Repository.AuthorityRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Service
public class AdminService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AdminEmailService adminEmailService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRegistrationLinksRepository accountRegistrationLinksRepository;

    // @Autowired
    // private AdminEmailService adminEmailService;
    

    // Wyświetl listę wszystkich kont
    public List<AccountModel> getAllAccounts(){
        return accountRepository.findAll();
    }

    // Wyświetl konto o podanym emailu
    public AccountModel getAccountByEmail(String accountEmail){
        return accountRepository.findOneByEmail(accountEmail).orElseThrow(() -> new RuntimeException("Nie znaleziono konta!"));
    }

    // Znajdź konto poprzez treść link aktywacyjny
    public AccountModel findOneByRegistrationLink(AccountRegistrationLinks link){
        return accountRepository.findOneByAccountRegistrationLinks(link).orElseThrow(() -> new RuntimeException("Nie znaleziono konta przypisanego do danego linku!"));
    }
    // Znajdź konto poprzez rolę
    public AccountModel findOneByAccountAuthority(String authority){
        return accountRepository.findOneByAuthorities(authorityRepository.findById(authority).orElseThrow(() -> new RuntimeException("Nie znaleziono podanej rolii"))).orElseThrow(() -> new RuntimeException("Nie znaleziono konta po podanej roli!"));
    }
    // Znajdź konto poprzez id konta
    public AccountModel findOneById(String id){
        return accountRepository.findById(Long.parseLong(id)).orElseThrow(() -> new RuntimeException("Nie znaleziono konta o podanym id!"));
    }
    // Stwórz konto
    public AccountModel createAccount(AccountModel accountToCreate){
        System.out.println("WYWOŁANO CREATEACCOUNT");
        String firstPassword = "FishWebToJestSuperFirmaAFishmailJestSuper";
        Set<AuthorityModel> userAuthorithies = new HashSet<>();
        authorityRepository.findById("ROLE_USER").ifPresent(userAuthorithies::add);
        accountToCreate.setAuthorities(userAuthorithies);
        accountToCreate.setPassword(passwordEncoder.encode(firstPassword));
        accountToCreate.setEnabled(false);
        accountToCreate.setActive(true);
        accountToCreate.setBlocked(false);
        accountToCreate.setCreatedaccountTime(LocalDateTime.now());
        accountToCreate.setValidAccountTime(LocalDateTime.now().plusDays(30));
        AccountRegistrationLinks registrationLink = new AccountRegistrationLinks();
        String activationLink = UUID.randomUUID().toString();
        registrationLink.setActivationCode(activationLink);
        registrationLink.setExpirationDate(LocalDateTime.now().plusHours(12));
        registrationLink.setValid(true);
        registrationLink.setAccount(accountToCreate);
        accountToCreate.setAccountRegistrationLinks(registrationLink);
        adminEmailService.sendRegisterEmail(registrationLink);
        return accountRepository.save(accountToCreate);
    }

    // Usuń konto
    public void adminDeleteAccount(String accountEmail){
        AccountModel accountToDelete = accountRepository.findOneByEmail(accountEmail).orElseThrow(() -> new RuntimeException("Nie znaleziono konta"));

        accountRepository.delete(accountToDelete);
    }
    
    // Aktywuj konto poprzez link aktywacyjny
    public void activateAccount(String activationLink){
        System.out.println("Wywołano funkcję activate account");
        LocalDateTime now = LocalDateTime.now();
        AccountRegistrationLinks linkAccount = accountRegistrationLinksRepository.findOneByActivationCode(activationLink);
  
        System.out.println(linkAccount.getAccount().getEmail());
        System.out.println(linkAccount.getActivationCode());
        // if(linkAccount.getExpirationDate().isBefore(now)){
        if(now.isBefore(linkAccount.getExpirationDate()) && linkAccount.isValid() && linkAccount != null){

            System.out.println("Warunek czasu sprawdzony");
            // AccountModel accountToActivate = accountRepository.findOneByAccountRegistrationLinks(linkAccount).orElseThrow(() -> new RuntimeException("Nie znaleziono konta po linku aktywacyjnym!"));
            AccountModel accountToActivate = linkAccount.getAccount();
            System.out.println(accountToActivate.getEmail());
            accountToActivate.setEnabled(true);
            // linkAccount.setValid(false);
            accountRepository.save(accountToActivate);
    

        } else {
            System.out.println("Link nie istnieje");
        }
    }
@Transactional
public void reSendActivationLink(String accountEmail) {
    // Znajdź konto na podstawie e-maila
    AccountModel accountToResend = accountRepository.findOneByEmail(accountEmail)
        .orElseThrow(() -> new RuntimeException("Nie znaleziono konta o podanym email!"));

    // Sprawdź, czy konto już ma link aktywacyjny
    AccountRegistrationLinks oldLinkToDelete = accountToResend.getAccountRegistrationLinks();
    if (oldLinkToDelete != null) {
        // Zaktualizuj istniejący link, jeśli istnieje
        String activationCode = UUID.randomUUID().toString();
        oldLinkToDelete.setActivationCode(activationCode);
        oldLinkToDelete.setValid(true);
        oldLinkToDelete.setExpirationDate(LocalDateTime.now().plusHours(12));
        accountRegistrationLinksRepository.save(oldLinkToDelete); // Zapisz zaktualizowany link
        return;
    }

    // Utwórz nowy link aktywacyjny, jeśli nie istnieje
    AccountRegistrationLinks newLink = new AccountRegistrationLinks();
    String activationCode = UUID.randomUUID().toString();
    newLink.setActivationCode(activationCode);
    newLink.setExpirationDate(LocalDateTime.now().plusHours(12));
    newLink.setValid(true);
    newLink.setAccount(accountToResend);

    // Zapisz nowy link
    accountRegistrationLinksRepository.save(newLink);
    
    // Przypisz nowy link do konta
    accountToResend.setAccountRegistrationLinks(newLink);
    accountRepository.save(accountToResend);
    adminEmailService.sendRegisterEmail(newLink);
}

    // Ustaw hasło dla konta
    public void setUserPassword(String activationId,String userPassword){
        AccountRegistrationLinks registrationLink = accountRegistrationLinksRepository.findOneByActivationCode(activationId);

        if(registrationLink.isValid()){
        AccountModel userAccountPasswordToChange = registrationLink.getAccount();
        System.out.println(userAccountPasswordToChange.getEmail());
        System.out.println(userPassword);
        userAccountPasswordToChange.setPassword(passwordEncoder.encode(userPassword));
        registrationLink.setValid(false);
        accountRegistrationLinksRepository.save(registrationLink);
        accountRepository.save(userAccountPasswordToChange);
        } else {
            throw new RuntimeException("Link aktywacyjny jest nie ważny!");
        }


    }


    // Przedłuż działanie konta
    public void redeemUserAccountValidity(String accountEmail){
        System.out.println("Wywołano redeem");
        AccountModel accountToRedeem = accountRepository.findOneByEmail(accountEmail).orElseThrow(() -> new RuntimeException("Nie znaleziono konta"));
        accountToRedeem.setValidAccountTime(accountToRedeem.getValidAccountTime().plusDays(30));
        accountRepository.save(accountToRedeem);
    }

    // Zablokuj konto
    @Transactional
    public void blockUserAccount(String accountEmail ){
        System.out.println("Wywołano block user");
        AccountModel accountToBlock = accountRepository.findOneByEmail(accountEmail).orElseThrow(() -> new RuntimeException("Nie znaleziono konta"));
        accountToBlock.setBlocked(true);
        accountRepository.save(accountToBlock);
    }
}
