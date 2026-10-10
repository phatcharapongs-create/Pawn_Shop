package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.LedgerEntry;
import com.kku.pawnshop.domain.enums.LedgerEntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    
    // เพิ่มบรรทัดนี้: ค้นหารายการทั้งหมดที่เกิดขึ้นในวันที่กำหนด
    List<LedgerEntry> findByEntryDate(LocalDate entryDate);

    boolean existsByTicket_IdAndEntryTypeAndEntryDate(Long ticketId, LedgerEntryType entryType, LocalDate entryDate);
    
}
