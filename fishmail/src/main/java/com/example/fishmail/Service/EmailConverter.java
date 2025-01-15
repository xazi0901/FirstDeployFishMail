package com.example.fishmail.Service;

import org.springframework.stereotype.Service;

import com.example.fishmail.Models.EmailDTO;
import com.example.fishmail.Models.EmailModel;

@Service
public class EmailConverter {

    public EmailDTO toDTO(EmailModel emailModel) {
        return new EmailDTO(
            emailModel.getId(),
            emailModel.getTitle(),
            emailModel.getMessageBody(),
            emailModel.getSendTime(),
            emailModel.getSendDate(),
            emailModel.getCampaign().getId(),
            emailModel.getStatus()
        );
    }
}
