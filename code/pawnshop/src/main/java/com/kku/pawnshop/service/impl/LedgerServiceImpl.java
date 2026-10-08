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

@Service
public class LedgerServiceImpl implements LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;

    // =================================================================
    // นี่คือการทำ Constructor Injection ด้วยตัวเอง (ถูกกฎโปรเจกต์ 100%)
    // แก้ปัญหา Lombok ไม่ทำงาน และทำให้ตัวแปร final ไม่ขึ้น Error ครับ
    // =================================================================
    public LedgerServiceImpl(LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Override
    @Transactional
    public LedgerEntry recordEntry(PawnTicket ticket, LedgerEntryType entryType, 
                                   Money principal, Money interest, Money total, 
                                   LocalDate interestFrom, LocalDate interestTo, 
                                   Employee handledBy, String note) {
        
        // สร้างรายการบัญชีใหม่ (อาศัย Constructor ที่เราสร้างไว้ใน Entity)
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
        Money reversedPrincipal = new Money(original.getPrincipalAmount().getAmount().negate());
        Money reversedInterest = new Money(original.getInterestAmount().getAmount().negate());
        Money reversedTotal = new Money(original.getTotalAmount().getAmount().negate());

        // 3. แนบเหตุผลลงใน Note ว่าเป็นการกลับรายการของ ID ไหน
        String reversalNote = String.format("กลับรายการของรหัส %d | เหตุผล: %s", originalEntryId, reason);

        // 4. บันทึกรายการใหม่ลงไป ด้วยประเภทการกลับรายการ
        LedgerEntry reversalEntry = new LedgerEntry(
                original.getTicket(),
                LedgerEntryType.REVERSAL, // ตรวจสอบใน LedgerEntryType ว่าเพื่อนสร้างค่า REVERSAL หรือยัง
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