package com.example.fishmail.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fishmail.Models.SendingModel;

@Repository
public interface SendingRepository extends JpaRepository<SendingModel,Long>{
    
}
