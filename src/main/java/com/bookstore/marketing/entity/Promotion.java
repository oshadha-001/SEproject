package com.bookstore.marketing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email subject is required")
    @Column(nullable = false, length = 200)
    private String subject;

    @NotBlank(message = "Email content is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @NotBlank(message = "Target audience is required")
    @Column(nullable = false, length = 100)
    private String targetAudience; // e.g., "All Customers", "VIP Customers", "New Subscribers"

    @Column(nullable = false, length = 20)
    private String status = "DRAFT"; // DRAFT, SCHEDULED, SENT, CANCELLED

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Column(name = "recipient_count")
    private Integer recipientCount = 0;

    @Column(columnDefinition = "TEXT")
    private String discountCode;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}