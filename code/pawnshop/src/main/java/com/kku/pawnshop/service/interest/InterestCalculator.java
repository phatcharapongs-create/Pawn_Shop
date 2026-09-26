package com.kku.pawnshop.service.interest;

import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.vo.Money;

import java.time.LocalDate;

/**
 * คำนวณดอกเบี้ยค้างชำระของตั๋ว
 *
 * DIP: TicketOperationService ขึ้นกับอินเทอร์เฟซนี้เท่านั้น
 * ไม่รู้ว่าเบื้องหลังคิดแบบขั้นบันไดหรือแบบอัตราเดียว
 */
public interface InterestCalculator {

    /** ดอกเบี้ยค้างตั้งแต่ ticket.interestPaidUntil ถึงวันที่กำหนด */
    Money accruedInterest(PawnTicket ticket, LocalDate asOf);

    /** ยอดรวมที่ต้องจ่ายเพื่อไถ่ถอน = เงินต้น + ดอกค้าง */
    default Money redemptionAmount(PawnTicket ticket, LocalDate asOf) {
        return ticket.getPrincipal().plus(accruedInterest(ticket, asOf));
    }
}
