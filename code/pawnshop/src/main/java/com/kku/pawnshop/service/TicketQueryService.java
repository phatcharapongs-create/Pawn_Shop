package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.vo.Money;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * ISP: อินเทอร์เฟซสำหรับ "การอ่านอย่างเดียว" — เจ้าของ: สมาชิก C
 *
 * แยกจาก TicketOperationService และ TicketAdminService เพราะ
 * หน้าจอที่ให้ลูกค้าดูสถานะตั๋วต้องการแค่เมธอดชุดนี้
 * การแยกแบบนี้ทำให้คลาสที่รับแต่ TicketQueryService
 * ไม่มีทางเรียก forceForfeit() ได้เลยตั้งแต่ระดับ type
 */
public interface TicketQueryService {

    PawnTicket findById(Long id);

    PawnTicket findByTicketNumber(String ticketNumber);

    Page<PawnTicket> findAll(Pageable pageable);

    Page<PawnTicket> findByCustomer(Long customerId, Pageable pageable);

    /** ยอดที่ต้องจ่ายถ้าจะไถ่ถอนวันนี้ = เงินต้น + ดอกค้าง */
    Money quoteRedemptionAmount(Long ticketId, LocalDate asOf);

    /** ดอกที่ต้องจ่ายถ้าจะต่อดอกวันนี้ */
    Money quoteRenewalInterest(Long ticketId, LocalDate asOf);
}
