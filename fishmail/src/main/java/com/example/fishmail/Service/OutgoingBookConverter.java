package com.example.fishmail.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Models.OutgoingBook;
import com.example.fishmail.Models.OutgoingBookDTO;
import com.example.fishmail.Models.SendingModel;
import com.example.fishmail.Models.Enum.SendingStatus;



@Service
public class OutgoingBookConverter {
    @Autowired
    private EmailService emailService;
    
        public OutgoingBookDTO toDTO(OutgoingBook outgoingBook) {
        return new OutgoingBookDTO(
            outgoingBook.getId(),
            outgoingBook.getEmail().getId(),
            outgoingBook.getRecivierEmail(),
            outgoingBook.getTrackingId(),
            outgoingBook.isOpened(),
            outgoingBook.getSendTime(),
            outgoingBook.getSendDate(),
            outgoingBook.getStatus()
        );
    }

    public OutgoingBook FromDTOtoEnum(OutgoingBookDTO outgoingBookDTO){
        EmailModel email = emailService.findOneById(String.valueOf(outgoingBookDTO.getEmailID()));
        return new OutgoingBook(
            outgoingBookDTO.getId(),
            outgoingBookDTO.getRecivierEmail(),
            outgoingBookDTO.getTrackingId(),
            outgoingBookDTO.isIsOpened(),
            outgoingBookDTO.getSendTime(),
            outgoingBookDTO.getSendDate(),
            outgoingBookDTO.getEmailID(),
            outgoingBookDTO.getStatuses()
        );
    }
}
