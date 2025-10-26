// StaffRepository.java
package com.booknest.booknest.repository;

import com.booknest.booknest.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {}