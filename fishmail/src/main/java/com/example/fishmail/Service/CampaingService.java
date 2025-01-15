package com.example.fishmail.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Models.RecieversModel;
import com.example.fishmail.Models.Enum.CampaingStatus;
import com.example.fishmail.Models.Enum.EmailStatus;
import com.example.fishmail.Repository.AccountRepository;
import com.example.fishmail.Repository.CampaingRepository;
import com.example.fishmail.Repository.EmailRepository;
import com.example.fishmail.Repository.RecieversRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CampaingService {
    
    @Autowired
    private CampaingRepository campaingRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EmailRepository emailRepository;

    // @Autowired
    // private RecieversRepository recieversRepository;

    // @Autowired
    // private EmailRepository emailRepository;

    public Optional<CampaignModel> findOneById(String id){
        return campaingRepository.findById(Long.parseLong(id));
    }

    public List<CampaignModel> getAllCampaings(){
        return campaingRepository.findAll();
    }

    public CampaignModel createCampaing(CampaignModel campaing, String accountEmail){
        AccountModel account = accountRepository.findOneByEmail(accountEmail).orElseThrow(() -> new RuntimeException("Nie znaleziono konta"));
        campaing.setAccount(account);

        List<EmailModel> emails = campaing.getEmails();
        for (EmailModel email : emails) {
        email.setCampaign(campaing);
        email.setStatus(EmailStatus.W_TRAKCIE);
    }
        campaing.setStatus(CampaingStatus.W_TRAKCIE);
        return campaingRepository.save(campaing);
    }


    public void updateCampaingStatusAndCheckEmailStatus(CampaignModel campaing){
        List<EmailModel> emailsFromCapaing = emailRepository.findAllByCampaign(campaing);

        boolean allCompleted = emailsFromCapaing.stream().allMatch(email -> email.getStatus() == EmailStatus.ZAKOŃCZONA || email.getStatus() == EmailStatus.NIEUDANA);

        if (allCompleted) {
            campaing.setStatus(CampaingStatus.ZAKOŃCZONA);
            campaingRepository.save(campaing);
        }
    }

    public void deleteCampaingById(String id){
         campaingRepository.deleteById(Long.valueOf(id));

    }
}
