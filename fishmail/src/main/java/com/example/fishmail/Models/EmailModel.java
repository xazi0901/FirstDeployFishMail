package com.example.fishmail.Models;

import java.util.List;

import com.example.fishmail.Models.Enum.EmailStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "email")
public class EmailModel  {
    
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name ="id")
    private Long id;

    private String title;

    private String messageBody;

    private String sendTime;

    private String sendDate;

    @ManyToOne
    @JoinColumn(name = "campaing_id", nullable = false)
    private CampaignModel campaign;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EmailStatus status;


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessageBody() {
        return this.messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getSendTime() {
        return this.sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendDate() {
        return this.sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public CampaignModel getCampaign() {
        return this.campaign;
    }

    public void setCampaign(CampaignModel campaign) {
        this.campaign = campaign;
    }

    public EmailStatus getStatus() {
        return this.status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

}
