package com.management.student_center.repository;

import com.management.student_center.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    // Để trống là OK, JpaRepository đã đủ
}