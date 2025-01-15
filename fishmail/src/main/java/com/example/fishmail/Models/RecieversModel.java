package com.example.fishmail.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="recievers")
public class RecieversModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Long id;

    private String recieverEmail;

    // private String trackingId;

    @ManyToOne
    private CampaignModel campaign;

    // Konstruktor dla odbiorców
    public RecieversModel(String recieverEmail) {
    this.recieverEmail = recieverEmail;
        }
    public RecieversModel(String recieverEmail,CampaignModel campaign) {
    this.recieverEmail = recieverEmail;
    this.campaign = campaign;
}
}
