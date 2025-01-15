package com.example.fishmail.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fishmail.Models.AccountModel;
import com.example.fishmail.Models.AccountRegistrationLinks;
import com.example.fishmail.Models.AuthorityModel;
import com.example.fishmail.Models.OutgoingBook;

@Repository
public interface AccountRepository extends JpaRepository<AccountModel,Long>  {
    
    Optional<AccountModel> findOneByEmail(String email);

    Optional<AccountModel> findOneByAuthorities(AuthorityModel authorityModel);

    Optional<AccountModel> findOneByAccountRegistrationLinks(AccountRegistrationLinks link);

    public List<AccountModel> findAccountsByValidAccountTime(LocalDateTime now);

    // Optional<AccountModel> findAccountByActivationCode(String activationCode);
}
