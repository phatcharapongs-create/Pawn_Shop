package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.InterestPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/** เจ้าของ: สมาชิก D */
@Repository
public interface InterestPolicyRepository extends JpaRepository<InterestPolicy, Long> {

    /** นโยบายที่บังคับใช้ ณ วันที่กำหนด ใช้ตอนออกตั๋วใหม่ */
    @Query("""
            SELECT p FROM InterestPolicy p
            WHERE p.effectiveFrom <= :date
              AND (p.effectiveTo IS NULL OR p.effectiveTo >= :date)
            ORDER BY p.effectiveFrom DESC
            LIMIT 1
            """)
    Optional<InterestPolicy> findEffectiveOn(@Param("date") LocalDate date);
}
