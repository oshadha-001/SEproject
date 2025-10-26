package com.bookstore.marketing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(name = "emails_delivered", nullable = false)
    private Integer emailsDelivered = 0;

    @Column(name = "emails_opened", nullable = false)
    private Integer emailsOpened = 0;

    @Column(name = "links_clicked", nullable = false)
    private Integer linksClicked = 0;

    @Column(name = "unsubscribes", nullable = false)
    private Integer unsubscribes = 0;

    @Column(name = "open_rate", nullable = false)
    private Double openRate = 0.0; // Percentage

    @Column(name = "click_through_rate", nullable = false)
    private Double clickThroughRate = 0.0; // Percentage

    @Column(name = "unsubscribe_rate", nullable = false)
    private Double unsubscribeRate = 0.0; // Percentage

    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
        calculateRates();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
        calculateRates();
    }

    private void calculateRates() {
        if (emailsDelivered > 0) {
            openRate = (emailsOpened * 100.0) / emailsDelivered;
            clickThroughRate = (linksClicked * 100.0) / emailsDelivered;
            unsubscribeRate = (unsubscribes * 100.0) / emailsDelivered;
        }
    }
}