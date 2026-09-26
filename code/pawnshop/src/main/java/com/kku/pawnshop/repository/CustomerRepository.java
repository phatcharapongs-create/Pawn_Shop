package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** เจ้าของ: สมาชิก A */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCitizenId(String citizenId);

    boolean existsByCitizenId(String citizenId);

    /** ใช้กับ endpoint ที่ต้องมี pagination และ sorting ตามใบงาน */
    Page<Customer> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);
}
