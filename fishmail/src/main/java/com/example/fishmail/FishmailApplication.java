package com.example.fishmail;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.fishmail")
@EnableScheduling
@EnableAsync
public class FishmailApplication {

	public static void main(String[] args) {
		 TimeZone.setDefault(TimeZone.getTimeZone("Europe/Warsaw"));
		SpringApplication.run(FishmailApplication.class, args);
	}

}
