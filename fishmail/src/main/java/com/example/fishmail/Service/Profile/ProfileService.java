package com.example.fishmail.Service.Profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Service.AccountService;

@Service
public class ProfileService {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AccountModel getUserProfile(String userEmail){
        return accountService.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Nie znaleziono profilu"));
    }

    public AccountModel editUserProfile(AccountModel profileDataToChange,String userEmail){
        AccountModel accountToEdit = accountService.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Nie znaleziono profilu"));
        if(!profileDataToChange.getEmail().isEmpty()) accountToEdit.setEmail(profileDataToChange.getEmail());
        if(!profileDataToChange.getName().isEmpty()) accountToEdit.setName((profileDataToChange.getName()));
        if(!profileDataToChange.getPassword().isEmpty()) accountToEdit.setPassword(passwordEncoder.encode(profileDataToChange.getPassword()));
        if(!profileDataToChange.getSmtpApiKey().isEmpty()) accountToEdit.setSmtpApiKey(profileDataToChange.getSmtpApiKey());
        if(!profileDataToChange.getSmtpHost().isEmpty()) accountToEdit.setSmtpHost(profileDataToChange.getSmtpHost());
        if(!profileDataToChange.getSmtpLogin().isEmpty()) accountToEdit.setSmtpLogin(profileDataToChange.getSmtpLogin());
        if(!profileDataToChange.getSmtpPassword().isEmpty()) accountToEdit.setSmtpPassword(profileDataToChange.getSmtpPassword());
        if(profileDataToChange.getSmtpPort() != 0) accountToEdit.setSmtpPort(profileDataToChange.getSmtpPort());

        return accountService.save(accountToEdit);

    }
}
