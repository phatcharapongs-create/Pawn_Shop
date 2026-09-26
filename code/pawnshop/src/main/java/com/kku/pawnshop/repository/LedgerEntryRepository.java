package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.LedgerEntry;
import com.kku.pawnshop.domain.enums.LedgerEntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * เจ้าของ: สมาชิก D
 *
 * ไม่มีเมธอด update หรือ delete เพราะ ledger เป็น append-only
 * ถ้าจำเป็นต้องแก้ ให้บันทึกรายการกลับรายการใหม่แทน
 */
@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByTicketIdOrderByEntryDateAscIdAsc(Long ticketId);

    List<LedgerEntry> findByEntryDate(LocalDate entryDate);

    List<LedgerEntry> findByEntryDateAndEntryType(LocalDate entryDate, LedgerEntryType entryType);
}
