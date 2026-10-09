package com.kku.pawnshop.service.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kku.pawnshop.domain.entity.Employee;
import com.kku.pawnshop.domain.entity.LedgerEntry;
import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.enums.LedgerEntryType;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.repository.LedgerEntryRepository;
import com.kku.pawnshop.service.LedgerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // ใช้สไตล์เดียวกับเพื่อน 100%
public class LedgerServiceImpl implements LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;

    @Override
    @Transactional
    public LedgerEntry recordEntry(PawnTicket ticket, LedgerEntryType entryType, 
                                   Money principal, Money interest, Money total, 
                                   LocalDate interestFrom, LocalDate interestTo, 
                                   Employee handledBy, String note) {
        
        // สร้างรายการบัญชีใหม่
        LedgerEntry entry = new LedgerEntry(
                ticket, entryType, principal, interest, total, 
                interestFrom, interestTo, LocalDate.now(), handledBy, note
        );
        
        return ledgerEntryRepository.save(entry);
    }

    @Override
    @Transactional
    public LedgerEntry reverseEntry(Long originalEntryId, Employee handledBy, String reason) {
        // 1. ค้นหารายการเดิมที่บันทึกผิด
        LedgerEntry original = ledgerEntryRepository.findById(originalEntryId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบรายการบัญชีรหัส: " + originalEntryId));

        // 2. สร้างยอดเงินแบบติดลบ (Negate) เพื่อหักล้างยอดเดิม
        Money reversedPrincipal = Money.of(original.getPrincipalAmount().getAmount().negate());
        Money reversedInterest = Money.of(original.getInterestAmount().getAmount().negate());
        Money reversedTotal = Money.of(original.getTotalAmount().getAmount().negate());

        // 3. แนบเหตุผลลงใน Note ว่าเป็นการกลับรายการของ ID ไหน
        String reversalNote = String.format("กลับรายการของรหัส %d | เหตุผล: %s", originalEntryId, reason);

        // 4. บันทึกรายการใหม่ลงไป
        LedgerEntry reversalEntry = new LedgerEntry(
                original.getTicket(),
                original.getEntryType(), // <--- แก้ไขตรงนี้: ใช้ประเภทเดียวกับรายการเดิม
                reversedPrincipal,
                reversedInterest,
                reversedTotal,
                original.getInterestFrom(),
                original.getInterestTo(),
                LocalDate.now(),
                handledBy,
                reversalNote
        );

        return ledgerEntryRepository.save(reversalEntry);
    }
}