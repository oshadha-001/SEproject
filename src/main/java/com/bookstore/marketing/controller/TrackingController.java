package com.bookstore.marketing.controller;

import com.bookstore.marketing.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.TimeUnit;

@Controller
public class TrackingController {

    @Autowired
    private PromotionService promotionService;

    // A 1x1 transparent GIF image in byte array form
    private static final byte[] TRANSPARENT_GIF = {
            (byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x39, (byte) 0x61,
            (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x80, (byte) 0x00,
            (byte) 0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x2c, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x02,
            (byte) 0x02, (byte) 0x44, (byte) 0x01, (byte) 0x00, (byte) 0x3b
    };

    /**
     * Endpoint for the invisible tracking pixel.
     */
    @GetMapping("/track/open/{promotionId}")
    public ResponseEntity<byte[]> trackOpen(@PathVariable Long promotionId) {

        System.out.println("--- TRACKING EVENT --- Received open request for Promotion ID: " + promotionId);

        try {
            // Call the service to update the emailsOpened count
            promotionService.recordEmailOpen(promotionId);
        } catch (Exception e) {
            System.err.println("Error recording email open for ID " + promotionId + ": " + e.getMessage());
        }

        // Return a transparent 1x1 GIF with headers to prevent caching
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_GIF);
        headers.setContentLength(TRANSPARENT_GIF.length);

        // Prevent client-side caching of the tracking pixel
        headers.setCacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS).noCache().mustRevalidate());

        return ResponseEntity.ok().headers(headers).body(TRANSPARENT_GIF);
    }
}