package com.example.fishmail.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fishmail.Models.RecieversModel;
import com.example.fishmail.Repository.RecieversRepository;

@RestController
public class RecieversRestController {
    @Autowired
    private RecieversRepository recieversRepository;

@PostMapping("/kampania/aktualizuj-odbiorce/{id}")
@ResponseBody
public ResponseEntity<?> updateRecieverEmail(
        @PathVariable String id,
        @RequestBody Map<String, String> payload) {
    String updatedEmail = payload.get("recieverEmail");
    RecieversModel reciever = recieversRepository.findById(Long.parseLong(id))
            .orElseThrow(() -> new RuntimeException("Nie znaleziono odbiorcy"));

    reciever.setRecieverEmail(updatedEmail);
    recieversRepository.save(reciever);

    return ResponseEntity.ok().build();
}
}
