package com.example.fishmail.Service;

import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Repository.AccountRepository;

@Service
public class AccountService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    public AccountModel save(AccountModel account){
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    public Optional<AccountModel> findByEmail(String email){
        return accountRepository.findOneByEmail(email);
    }

    public void saveAccountFirstLoginSmtpProfile(Long accountId,String smtpLogin,String smtpPassword, String smtpHost, int smtpPort){
      AccountModel accountToUpdate = accountRepository.getReferenceById(accountId);
      accountToUpdate.setSmtpHost(smtpHost);
      accountToUpdate.setSmtpLogin(smtpLogin);
      accountToUpdate.setSmtpPassword(smtpPassword);
      accountToUpdate.setSmtpPort(smtpPort);
      accountToUpdate.setFirstLogin(false);
      accountRepository.save(accountToUpdate);
    }

    public boolean isFirstLogin(String accountEmail){
      AccountModel accountModel =  accountRepository.findOneByEmail(accountEmail).orElseThrow(() -> new RuntimeException("Nie znaleziono konta"));

      if(accountModel.isFirstLogin()){
        return true;
      } else {
        return false;
      }
    }
}
