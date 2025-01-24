package com.example.fishmail.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.fishmail.Models.AccountModel;

@Component("userDetailsService")
public class MyUserDetailsService implements UserDetailsService {
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
System.out.println("Attempting to load user by email: " + email);
        // boolean isEnabled = true;
        boolean accountNotExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNotBlocked = true;
        boolean firstLogin = true;
        Optional<AccountModel> optionalAccount = accountService.findByEmail(email);

        if (!optionalAccount.isPresent()) {
            throw new UsernameNotFoundException("Account not found");
        }

        AccountModel account = optionalAccount.get();
           // Użyj BCrypt do sprawdzenia hasła


        List<GrantedAuthority> grantedAuthorities = account
                .getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
System.out.println("User loaded: " + email);

        return new org.springframework.security.core.userdetails.User(account.getEmail(), account.getPassword(),account.isEnabled(),accountNotExpired,credentialsNonExpired,accountNotBlocked,
                grantedAuthorities);
    }
    }

