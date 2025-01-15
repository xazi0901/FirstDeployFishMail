package com.example.fishmail.Models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AdminEmail {
    
    private String title;

    private String messageBody;
}
