package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.LedgerEntry;
import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.vo.Money;

import java.time.LocalDate;
import java.util.List;

/** เจ้าของ: สมาชิก D */
public interface LedgerService {

    LedgerEntry recordPawn(PawnTicket ticket, Money principal, Long employeeId);

    LedgerEntry recordInterestPayment(PawnTicket ticket, Money interest,
                                      LocalDate from, LocalDate to, Long employeeId);

    LedgerEntry recordRedemption(PawnTicket ticket, Money principal, Money interest,
                                 LocalDate from, LocalDate to, Long employeeId);

    List<LedgerEntry> findByTicket(Long ticketId);

    /** ยอดรวมรับจ่ายประจำวัน ใช้ในรายงานปิดยอด */
    Money totalCollectedOn(LocalDate date);
}
