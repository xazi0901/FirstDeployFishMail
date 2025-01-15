package com.example.fishmail.Controller;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.fishmail.Service.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;

    @GetMapping("/images/{id}")
    public Resource getImage(@PathVariable("id") String imageUri) {
        return fileService.load(imageUri);
    }
}
