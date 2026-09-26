package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.vo.Money;

import java.time.LocalDate;

/**
 * ISP: ธุรกรรมที่พนักงานหน้าเคาน์เตอร์ทำได้ — เจ้าของ: สมาชิก C
 *
 * ทุกเมธอดในนี้ต้องเช็ค TicketState.canXxx() ก่อนเสมอ
 * ถ้าทำไม่ได้ให้โยน InvalidTicketOperationException ซึ่ง
 * GlobalExceptionHandler จะแปลงเป็น HTTP 409 Conflict
 */
public interface TicketOperationService {

    /** รับจำนำและออกตั๋วใหม่ */
    PawnTicket openTicket(Long customerId, Long pledgedItemId, Money requestedPrincipal, Long employeeId);

    /** ส่งดอกเพื่อขยายกำหนดไถ่ถอน */
    PawnTicket renewInterest(Long ticketId, LocalDate paymentDate, Long employeeId);

    /** ไถ่ถอน จ่ายเงินต้นพร้อมดอกค้าง */
    PawnTicket redeem(Long ticketId, LocalDate paymentDate, Long employeeId);
}
