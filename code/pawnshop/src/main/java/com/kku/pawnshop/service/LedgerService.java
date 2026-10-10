package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.Employee;
import com.kku.pawnshop.domain.entity.LedgerEntry;
import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.enums.LedgerEntryType;
import com.kku.pawnshop.domain.vo.Money;

import java.time.LocalDate;

public interface LedgerService {
    
    // บันทึกรายการปกติ (จ่ายต้น/ดอก)
    LedgerEntry recordEntry(PawnTicket ticket, LedgerEntryType entryType, 
                            Money principal, Money interest, Money total, 
                            LocalDate interestFrom, LocalDate interestTo, 
                            Employee handledBy, String note);

    // กลับรายการ (Compensating Entry) กรณีบันทึกผิด
    LedgerEntry reverseEntry(Long originalEntryId, Employee handledBy, String reason);

    boolean hasInterestPaymentOn(Long ticketId, LocalDate entryDate);
}
