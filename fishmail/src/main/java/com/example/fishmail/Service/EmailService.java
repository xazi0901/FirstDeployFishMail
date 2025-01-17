package com.example.fishmail.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Repository.EmailRepository;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private CampaingService campaingService;

    public EmailModel saveEmail(EmailModel email){
        return emailRepository.save(email);
    }

    public void saveVoidEmail(EmailModel email){
        emailRepository.save(email);
        CampaignModel campaing = email.getCampaign();
        if(campaing != null){
            campaingService.updateCampaingStatusAndCheckEmailStatus(campaing);
        }
    }

    public EmailModel findOneById(String id){
        return emailRepository.findById(Long.parseLong(id)).orElseThrow(() -> new RuntimeException("Nie znaleziono emaila o podanym id"));
    }

    public EmailModel findOneByLongId(Long id){
        return emailRepository.findById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono emaila o podanym id"));
    }

    // Usuń by id
    public void deleteEmailInCapmaingById(Long id){
        emailRepository.deleteById(id);
    }

    
    
}
