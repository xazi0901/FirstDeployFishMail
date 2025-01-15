package com.example.fishmail.RequestModels;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {

    private String title;

    private String body;

    @DateTimeFormat(pattern = "yyyy-MM-dd") 
    private LocalDate sendDate;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime sendTime;
    
    
}
