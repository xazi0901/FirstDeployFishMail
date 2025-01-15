package com.example.fishmail.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.Enum.CampaingStatus;

@Repository
public interface CampaingRepository extends JpaRepository<CampaignModel,Long>{
    
    long countByAccountAndStatus(AccountModel accountModel, CampaingStatus status);
}
