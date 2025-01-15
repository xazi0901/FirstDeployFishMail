package com.example.fishmail.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.RecieversModel;

@Repository
public interface RecieversRepository extends JpaRepository<RecieversModel, Long>{
    
    public Optional<RecieversModel> findOneByCampaignId(Long campaignId);

    public List<RecieversModel> findAllByCampaign(CampaignModel capaign);
}
