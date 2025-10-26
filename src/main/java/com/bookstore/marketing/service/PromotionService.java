package com.bookstore.marketing.service;

import aj.org.objectweb.asm.commons.Remapper;
import com.bookstore.marketing.entity.Analytics;
import com.bookstore.marketing.entity.Promotion;
import com.bookstore.marketing.repository.AnalyticsRepository;
import com.bookstore.marketing.repository.PromotionRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    private JavaMailSender mailSender;

    // --- Controller-Dependent Methods (Corrected signatures) ---

    public List<Promotion> findAll() {
        return promotionRepository.findAll();
    }

    public Optional<Promotion> findById(Long id) {
        return promotionRepository.findById(id);
    }

    @Transactional
    public Promotion save(Promotion promotion) {
        if (promotion.getId() == null) {
            promotion.setCreatedAt(LocalDateTime.now());
            promotion.setSentDate(null);
            promotion.setStatus("DRAFT");

            // 1. Save Promotion
            Promotion savedPromotion = promotionRepository.save(promotion);

            // 2. Initialize Analytics
            Analytics analytics = new Analytics();
            analytics.setPromotion(savedPromotion);
            analytics.setEmailsDelivered(0);
            analytics.setEmailsOpened(0);
            analyticsRepository.save(analytics);

            return savedPromotion;
        }
        promotion.setUpdatedAt(LocalDateTime.now());
        return promotionRepository.save(promotion);
    }

    public void deleteById(Long id) {
        // Delete the associated Analytics record first to prevent foreign key errors
        analyticsRepository.findByPromotionId(id).ifPresent(analytics -> {
            analyticsRepository.delete(analytics);
        });
        promotionRepository.deleteById(id);
    }

    // --- Core Email Sending & Tracking Logic ---

    @Transactional
    public void sendPromotion(Promotion promotion, String recipient) throws MessagingException {
        // --- 1. Prepare Email Content with Tracking Pixel ---
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("your-gmail-address@gmail.com"); // !!! UPDATE THIS !!!
        helper.setTo(recipient);
        helper.setSubject(promotion.getSubject());

        // Build the base HTML content
        String emailBody = "<html><body>"
                + "<h1>" + promotion.getSubject() + "</h1>"
                + "<p>Hello Recipient,</p>"
                + "<p>" + promotion.getContent() + "</p>"
                + "<p>Use discount code: <b>" + promotion.getDiscountCode() + "</b></p>"
                + "</body></html>";

        // 2. Create the tracking pixel URL
        String trackingUrl = "http://localhost:8080/track/open/" + promotion.getId();

        // 3. Embed the 1x1 invisible tracking pixel into the HTML content
        String trackingPixel = "<img src='" + trackingUrl + "' width='1' height='1' style='display:none;' />";
        String finalEmailBody = emailBody + trackingPixel;

        // CRITICAL FIX: Use helper.setText(..., true) to explicitly mark content as HTML
        helper.setText(finalEmailBody, true);

        // --- 4. Send Message and Update Status/Analytics ---
        mailSender.send(message);

        // Update Promotion status and date (done once per batch)
        if (!"SENT".equals(promotion.getStatus())) {
            promotion.setStatus("SENT");
            promotion.setSentDate(LocalDateTime.now());
            promotionRepository.save(promotion);
        }

        // Update Analytics: Increment delivered count for each email sent
        analyticsRepository.findByPromotionId(promotion.getId()).ifPresent(analytics -> {
            analytics.setEmailsDelivered(analytics.getEmailsDelivered() + 1);
            analyticsRepository.save(analytics);
        });
    }

    @Transactional
    public void recordEmailOpen(Long promotionId) {
        Optional<Analytics> analyticsOpt = analyticsRepository.findByPromotionId(promotionId);

        if (analyticsOpt.isPresent()) {
            Analytics analytics = analyticsOpt.get();

            // 1. Increment the emailsOpened count
            analytics.setEmailsOpened(analytics.getEmailsOpened() + 1);
            analytics.setLastUpdated(LocalDateTime.now());
            analyticsRepository.save(analytics);

            // 2. CRITICAL: Force the change to be written to the database immediately
            analyticsRepository.flush();

            System.out.println("--- DB UPDATE SUCCESS --- Opened count incremented for Promotion ID: " + promotionId);
        } else {
            System.err.println("CRITICAL ERROR: Analytics record not found for Promotion ID: " + promotionId);
        }
    }

    public Object getAllPromotions() {
        return null;
    }

    public void createPromotion(@Valid Promotion promotion) {
    }

    public Remapper getPromotionById(Long id) {
        return null;
    }

    public void updatePromotion(Long id, @Valid Promotion promotion) {
    }

    public void deletePromotion(Long id) {
    }
}