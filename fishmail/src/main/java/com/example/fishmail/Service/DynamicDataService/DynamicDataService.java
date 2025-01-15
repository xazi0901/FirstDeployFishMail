package com.example.fishmail.Service.DynamicDataService;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Models.CampaignModel;

@Service
public class DynamicDataService {
    
    // Przykład konwersacji AccountModel na dane tablicy
    public Map<String, Object> mapAccountModel(AccountModel account){
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("ID", account.getId());
        row.put("Name", account.getName());
        row.put("Email", account.getEmail());
        row.put("Password", account.getPassword());
        row.put("SMTP Host", account.getSmtpHost());
        row.put("SMTP Login", account.getSmtpLogin());
        row.put("SMTP Password", account.getSmtpPassword());
        return row;
    }

    public Map<String, Object> mapCampaignModel(CampaignModel campaign) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("ID", campaign.getId());
        row.put("Name", campaign.getName());
        row.put("Status", campaign.getStatus());
        row.put("Emails Count", campaign.getEmails().size());
        return row;
    }



    // Funkcja która będzie renderować tabele
    public List<Map<String,Object>> mapEntitiesToTable(List<?> entities){
        if(entities.isEmpty()){
            return Collections.emptyList();
        }
        Object firstEntity = entities.get(0);
        if(firstEntity instanceof AccountModel){
            return entities.stream().map(entity -> mapAccountModel((AccountModel) entity )).collect(Collectors.toList());
        } if (firstEntity instanceof CampaignModel){
            return entities.stream().map(entity -> mapCampaignModel((CampaignModel) entity)).collect(Collectors.toList());
        }
        throw new IllegalArgumentException("Nie wspierany model: " + firstEntity.getClass());
    }
}
