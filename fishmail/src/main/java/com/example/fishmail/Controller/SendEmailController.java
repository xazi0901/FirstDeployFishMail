package com.example.fishmail.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fishmail.RequestModels.EmailRequest;
import com.example.fishmail.RequestModels.ScheduleRequest;
import com.example.fishmail.Service.DynamicTaskService;

@RestController
public class SendEmailController {

    @Autowired
    private DynamicTaskService dynamicTaskService;

        @PostMapping("/send-email")
    public ResponseEntity<String> postMethodName(@RequestBody EmailRequest emailRequest) {
        //TODO: process POST request
        // sendEmailService.sendScheleduledEmail(emailRequest);
        System.out.println(emailRequest.getSendTime());
         System.out.println(emailRequest.getSendDate());
          System.out.println(emailRequest.getTitle());
           System.out.println(emailRequest.getBody());
        return ResponseEntity.ok().body("oke");
    }

    @PostMapping("/schedule")
    public ResponseEntity<String> postMethodName(@RequestBody ScheduleRequest scheduleRequest) {
        //Konwertujemy wartości z inputu na LocalDateTime
        LocalDate localDate = LocalDate.parse(scheduleRequest.getSendDate1()); // yyyy-MM-dd
        LocalTime localTime = LocalTime.parse(scheduleRequest.getSendTime1()); // HH:mm
        LocalDateTime dateTime = LocalDateTime.of(localDate, localTime);

        // Zaplanuj zadanie
        dynamicTaskService.scheduleTask(dateTime, () -> {
            System.out.println("Wykonuje zaplanowaną funkcję o" + dateTime);
        });
        
        return ResponseEntity.ok().body("Zadanie zaplanowane na: " + dateTime);
    }
    
}
