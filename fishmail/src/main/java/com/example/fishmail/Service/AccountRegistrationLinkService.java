package com.example.fishmail.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fishmail.Models.AccountRegistrationLinks;
import com.example.fishmail.Repository.AccountRegistrationLinksRepository;

@Service
public class AccountRegistrationLinkService {
    

    @Autowired
    private AccountRegistrationLinksRepository accountRegistrationLinksRepository;


    public boolean checkLinkValidity(String activationId){
        AccountRegistrationLinks userActivationLink = accountRegistrationLinksRepository.findOneByActivationCode(activationId);

        System.out.println(userActivationLink.isValid());
        System.out.println(userActivationLink != null);
        System.out.println(userActivationLink == null);

        boolean isValid = userActivationLink.isValid() && userActivationLink == null ?  true :  false;
        return isValid;
    }
}
