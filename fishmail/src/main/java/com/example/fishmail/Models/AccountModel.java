package com.example.fishmail.Models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="account")
public class AccountModel {
    
    // Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Nazwa
    private String name;
    // Email
    private String email;
    // Password
    private String password;
    // login smtp
    private String smtpLogin;
    // url do smtp
    private String smtpHost;
    // api key
    private String smtpApiKey;
    // haslo do smtp
    private String smtpPassword;
    // Port
    private int smtpPort;

    private boolean isBlocked;

    private boolean isActive;

    private boolean isEnabled;

    private boolean firstLogin = true;

   @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime validAccountTime;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdaccountTime;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_authority", joinColumns = {
    @JoinColumn(name = "account_id", referencedColumnName = "id") }, inverseJoinColumns = {
    @JoinColumn(name = "authority_name", referencedColumnName = "name") })
    private Set<AuthorityModel> authorities = new HashSet<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CampaignModel> campaing;

    @OneToOne(mappedBy = "account",cascade = CascadeType.ALL,orphanRemoval = true)
    private AccountRegistrationLinks accountRegistrationLinks;
}
