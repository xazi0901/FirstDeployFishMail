package com.example.fishmail.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Models.Enum.EmailStatus;

@Repository
public interface EmailRepository extends JpaRepository<EmailModel,Long> {

    // @Query("SELECT e FROM EmailModel e WHERE e.sendDate = :currentDate AND e.sendTime = :currentTime AND e.status = 'ZAPLANOWANA'")
    // List<EmailModel> findEmailsToSend(@Param("currentDate") String currentDate, @Param("currentTime") String currentTime);

    List<EmailModel> findAllByCampaign(CampaignModel campaign);

    long countByStatus(EmailStatus status);
    long countByCampaignAndStatus(CampaignModel campaign, EmailStatus status);
    @Query("SELECT e.status, COUNT(e) FROM EmailModel e WHERE e.campaign = :campaign GROUP BY e.status")
    List<Object[]> countEmailsByStatusForCampaign(@Param("campaign") CampaignModel campaign);
} 