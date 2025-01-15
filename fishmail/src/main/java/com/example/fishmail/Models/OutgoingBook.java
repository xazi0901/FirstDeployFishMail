package com.example.fishmail.Models;

import java.util.List;

import com.example.fishmail.Models.Enum.SendingStatus;

import jakarta.persistence.CascadeType;
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
@Table(name ="outgoingBook")
public class OutgoingBook {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String recivierEmail;

    private String trackingId;

    private boolean isOpened;

    private String sendTime;
    
    private String sendDate;

    @OneToMany(mappedBy = "outgoingBook", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<SendingModel> sendingStatus;
    // @OneToMany(mappedBy = "outgoingBook", cascade = CascadeType.ALL,orphanRemoval = true)
    // private SendingModel status;

    @ManyToOne
    @JoinColumn(name ="email_id", nullable = false)
    private EmailModel email;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SendingStatus status;



    public OutgoingBook(Long id, String recieverEmail, String trackingId, boolean isOpened, String sendTime, String sendDate, Long emailId,SendingStatus status ){
        this.id = id;
        this.recivierEmail = recieverEmail;
        this.trackingId = trackingId;
        this.isOpened = isOpened;
        this.sendTime = sendTime;
        this.sendDate = sendDate;
        this.email = new EmailModel();
        this.email.setId(emailId);
        this.status = status;
    }
}
