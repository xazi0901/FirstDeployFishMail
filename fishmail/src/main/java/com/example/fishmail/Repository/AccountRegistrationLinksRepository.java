package com.example.fishmail.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fishmail.Models.AccountRegistrationLinks;

@Repository
public interface AccountRegistrationLinksRepository extends JpaRepository<AccountRegistrationLinks,Long> {
    
    AccountRegistrationLinks findOneByActivationCode(String activationCode);
}
