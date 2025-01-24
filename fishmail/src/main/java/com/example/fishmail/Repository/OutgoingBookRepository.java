package com.example.fishmail.Repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.EmailModel;
import com.example.fishmail.Models.OutgoingBook;
import com.example.fishmail.Models.Enum.SendingStatus;


@Repository
public interface OutgoingBookRepository extends JpaRepository<OutgoingBook,Long> {

    public Optional<OutgoingBook> findByTrackingId(String trackingId);

    public List<OutgoingBook> findAllByEmailId(Long emailId);
    
    public List<OutgoingBook> findAllByEmail(EmailModel email);

    public OutgoingBook getOneByRecivierEmail(String recieverEmail);

    public List<OutgoingBook> findAllByRecivierEmail(String recieverEmail);

     @Query("SELECT e FROM OutgoingBook e WHERE e.sendDate = :currentDate AND e.sendTime = :currentTime")
     public List<OutgoingBook> findOutgoingBooksToSend(@Param("currentDate") String currentDate, @Param("currentTime") String currentTime);

     long countByEmailAndStatus(EmailModel email, SendingStatus status);

     @Query("SELECT e.status, COUNT(e) FROM OutgoingBook e WHERE e.email = :email GROUP BY e.status")
     List<Object[]> countOutgoingBooksByStatusForEmail(@Param("email") EmailModel email);

     

     @Query("SELECT e.isOpened, COUNT(e) FROM OutgoingBook e WHERE e.isOpened = :isOpened GROUP BY e.isOpened")
     List<Object[]> countOutgoingBooksByIsOpened(@Param("isOpened") boolean isOpened);

    //  List<OutgoingBook> findAllBySendingStatus(@Param("status") String status);
    List<OutgoingBook> findByStatusAndEmailId(SendingStatus status,Long emailId);


     List<OutgoingBook> findAllByIsOpened(boolean isOpened);

       long countByEmailAndIsOpenedTrue(EmailModel email);

      // List<OutgoingBook> findAllBySendingStatus(Sort sort);

    // long countByCampaignAndIsOpenedTrue(CampaignModel campaign);
} 
