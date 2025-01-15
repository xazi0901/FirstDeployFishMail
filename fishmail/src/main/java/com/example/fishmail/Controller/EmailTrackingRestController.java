package com.example.fishmail.Controller;

import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fishmail.Service.OutgoingBookService;

@Controller
@RequestMapping("/track")
public class EmailTrackingRestController {
 
    @Autowired
    private OutgoingBookService outgoingBookService;


    @GetMapping("/opened")
    public ResponseEntity<byte[]> trackOpened(@RequestParam("trackingId") String trackingId,@RequestParam("campaingId") String campaingId) {
        outgoingBookService.changeIsOpenedStatusForEmail(trackingId,campaingId);



          BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 0xFFFFFF); // Biały piksel

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpeg", baos);
            byte[] pixel = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(pixel, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // Zwrócenie obrazka 1x1 jako odpowiedzi
        // byte[] pixel = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xDB, (byte) 0x00, (byte) 0x43, (byte) 0x00, (byte) 0x00 };
        // HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.IMAGE_JPEG);
        // return new ResponseEntity<>(pixel, headers, HttpStatus.OK);
    }
}
