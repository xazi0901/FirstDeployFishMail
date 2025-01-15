package com.example.fishmail.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.management.RuntimeErrorException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
    private final Path root = Paths.get("./uploads");

    // Inicjacja folderu w którym posiadamy pliki
    public void init(){
        try{
            Files.createDirectories(root);
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    // Funkcja do zapisu pliku
    public void save (MultipartFile file){
        // Try
        try{
            // Za pomocą obiektu Files kopiujemy input streamem do naszego roota podbierając nazwę pliku
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // Wczytanie obrazu
    public Resource load(String filename){
        if(filename == null)
            return null;
        try{
            // Musimy ustalić ścieżkę do pliku
            Path file = root.resolve(filename);
            // Potrzebujemy zasobu który będzie dostępny pod konkretnym adresem URL
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()){
                System.out.println("Otworzono zdjęcie");
                return resource;
            } else {
                throw new RuntimeException("Nie można wczytać obrazku");
            }
        } catch (MalformedURLException e){
            throw new RuntimeException("Błąd: " + e.getMessage());
        }
    }


}
