package com.example.fishmail.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fishmail.Models.AuthorityModel;

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityModel,String> {
    
}
