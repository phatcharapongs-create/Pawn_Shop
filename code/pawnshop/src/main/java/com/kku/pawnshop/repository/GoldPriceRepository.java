package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.GoldPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/** เจ้าของ: สมาชิก B */
@Repository
public interface GoldPriceRepository extends JpaRepository<GoldPrice, Long> {

    Optional<GoldPrice> findByPriceDate(LocalDate priceDate);

    /** ราคาล่าสุดที่ไม่เกินวันที่กำหนด ใช้เมื่อวันนั้นยังไม่ได้บันทึกราคา */
    Optional<GoldPrice> findTopByPriceDateLessThanEqualOrderByPriceDateDesc(LocalDate date);
}
