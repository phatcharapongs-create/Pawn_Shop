package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.SaleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** เจ้าของ: สมาชิก E */
@Repository
public interface SaleRecordRepository extends JpaRepository<SaleRecord, Long> {

    Optional<SaleRecord> findByPledgedItemId(Long pledgedItemId);

    List<SaleRecord> findBySoldDateBetween(LocalDate from, LocalDate to);
}
