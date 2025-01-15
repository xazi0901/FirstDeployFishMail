package com.example.fishmail.Config;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Models.AuthorityModel;
import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Models.RecieversModel;
import com.example.fishmail.Repository.AuthorityRepository;
import com.example.fishmail.Service.AccountService;
import com.example.fishmail.Service.FileService;



@Component
public class SeedData implements CommandLineRunner {

        @Autowired
        private FileService fileService;

        @Autowired
        private AccountService accountService;

        @Autowired
        private AuthorityRepository authorityRepository;
        @Override
        public void run(String... args) throws Exception {

                fileService.init();

                Optional<AccountModel> optionalAccount = accountService.findByEmail("test@gmail.com");

                if(!optionalAccount.isPresent()){
                        AuthorityModel user = new AuthorityModel();
                        user.setName("ROLE_USER");
                        authorityRepository.save(user);

                        AuthorityModel admin = new AuthorityModel();
                        admin.setName("ROLE_ADMIN");
                        authorityRepository.save(admin);

                        AccountModel account1 = new AccountModel();
                        account1.setEmail("test@gmail.com");
                        // account1.setPassword("test123");
                        account1.setPassword("test");
                        Set<AuthorityModel> authorities1 = new HashSet<>();
                        authorityRepository.findById("ROLE_ADMIN").ifPresent(authorities1::add);
                        account1.setAuthorities(authorities1);
                         account1.setEnabled(true);
                         account1.setActive(true);
                         account1.setSmtpHost("fishweb.pl");
                         account1.setSmtpLogin("ncleantest@nclean.pl");
                         account1.setSmtpPassword("NcleanTest");
                         account1.setSmtpPort(587);
                        account1.setName("FishWeb");
                        account1.setCreatedaccountTime(LocalDateTime.now());
                        account1.setValidAccountTime(LocalDateTime.now().plusDays(30));
                        accountService.save(account1);

                        // AccountModel account2 = new AccountModel();
                        // account2.setEmail("admin@fishweb.pl");
                        // account2.setPassword("test1");
                        // Set<AuthorityModel> authorithies2 = new HashSet<>();
                        // authorityRepository.findById("ROLE_ADMIN").ifPresent(authorithies2::add);
                        // account2.setAuthorities(authorithies2);
                        // account2.setEnabled(true);
                        // account2.setActive(true);
                        // accountService.save(account2);

                        // CampaignModel campaing = new CampaignModel();
                        // campaing.setAccount(account1);
                        // campaing.setName("Przykładowa kampania");
                        // EmailModel emailModel = new EmailModel();
                        // emailModel.setMessageBody("blabla");
                        // emailModel.setTitle("jakas kampania");
                        
                        // campaing.setEmails(emailModel);
                        // campaing.setRecivers(null);
                        
                }



        }
}

