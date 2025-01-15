package com.example.fishmail.Models;

import java.io.Serializable;
import java.util.List;

import com.example.fishmail.Models.Enum.SendingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutgoingBookDTO implements Serializable{
    @JsonProperty
    private Long id;
    @JsonProperty
    private String recivierEmail;
    @JsonProperty
    private String trackingId;
    @JsonProperty
    private boolean isOpened;
    @JsonProperty
    private String sendTime;
    @JsonProperty
    private String sendDate;
    @JsonProperty
    private SendingStatus status;
    @JsonProperty
    private Long emailID;

    public OutgoingBookDTO(){

    }

    public OutgoingBookDTO(Long id,Long emailID, String recieverEmail,String trackingId,boolean isOpened,String sendTime,String sendDate,SendingStatus status){
        this.id = id;
        this.recivierEmail = recieverEmail;
        this.trackingId = trackingId;
        this.isOpened = isOpened;
        this.sendTime = sendTime;
        this.sendDate = sendDate;
        this.status = status;
        this.emailID = emailID;
    }
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecivierEmail() {
        return this.recivierEmail;
    }

    public void setRecivierEmail(String recivierEmail) {
        this.recivierEmail = recivierEmail;
    }

    public String getTrackingId() {
        return this.trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public boolean isIsOpened() {
        return this.isOpened;
    }

    public boolean getIsOpened() {
        return this.isOpened;
    }

    public void setIsOpened(boolean isOpened) {
        this.isOpened = isOpened;
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

    public SendingStatus getStatuses() {
        return this.status;
    }

    public void setStatuses(SendingStatus statuses) {
        this.status = statuses;
    }

    public Long getEmailID() {
        return this.emailID;
    }

    public void setEmailID(Long emailID) {
        this.emailID = emailID;
    }

    
}
