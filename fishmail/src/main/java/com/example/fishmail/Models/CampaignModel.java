package com.example.fishmail.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.fishmail.Models.Enum.CampaingStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="campaing")
public class CampaignModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Long id;
    
    private String name;
    

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecieversModel> recivers = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private List<EmailModel> emails;

    // @OneToMany
    // private Set<SendingModel> sending;

    @ManyToOne
    private AccountModel account;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CampaingStatus status;

@Override
public String toString() {
    return "CampaignModel{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", emails=" + emails +
            ", recivers=" + recivers +
            ", status=" + status +
            '}';
}
}
