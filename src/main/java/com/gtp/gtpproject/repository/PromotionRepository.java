// PromotionRepository.java
package com.booknest.booknest.repository;

import com.booknest.booknest.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {}