package com.bookstore.marketing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Campaign name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Description is required")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Discount percentage is required")
    @Min(value = 1, message = "Discount must be at least 1%")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    @Column(nullable = false)
    private Double discountPercentage;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(nullable = false)
    private LocalDate endDate;

    @NotBlank(message = "Campaign type is required")
    @Column(nullable = false, length = 50)
    private String campaignType; // e.g., "Black Friday", "End of Year", "School Holiday"

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}