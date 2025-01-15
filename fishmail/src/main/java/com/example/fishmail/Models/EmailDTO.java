package com.example.fishmail.Models;

import lombok.Data;

import com.example.fishmail.Models.Enum.EmailStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;



// @Data
// @AllArgsConstructor
// @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailDTO implements Serializable {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String title;
    @JsonProperty
    private String messageBody;
    @JsonProperty
    private String sendTime;
    @JsonProperty
    private String sendDate;
    @JsonProperty
    private Long campaignId; // ID kampanii zamiast całego obiektu
    @JsonProperty
    private EmailStatus status;


public EmailDTO() {
    // Pusty konstruktor
}

public EmailDTO(Long id, String title, String messageBody, String sendTime, String sendDate, Long campaignId, EmailStatus status) {
    this.id = id;
    this.title = title;
    this.messageBody = messageBody;
    this.sendTime = sendTime;
    this.sendDate = sendDate;
    this.campaignId = campaignId;
    this.status = status;
}

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

    public Long getCampaignId() {
        return this.campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public EmailStatus getStatus() {
        return this.status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }
}
