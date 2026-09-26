package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.PawnTicket;

import java.time.LocalDate;
import java.util.List;

/**
 * ISP: คำสั่งระดับผู้จัดการและงานอัตโนมัติ — เจ้าของ: สมาชิก C
 *
 * แยกออกมาเพราะไม่ควรให้ controller ฝั่งลูกค้ามองเห็นเมธอดกลุ่มนี้เลย
 */
public interface TicketAdminService {

    /** อายัดตั๋วตามคำสั่งเจ้าพนักงาน ระงับทุกธุรกรรม */
    PawnTicket seize(Long ticketId, String reason, Long employeeId);

    /** ปลดอายัด กลับสู่สถานะเดิมตามวันที่ */
    PawnTicket releaseSeizure(Long ticketId, Long employeeId);

    /** งานประจำวัน: เลื่อนตั๋วที่เลยกำหนดเข้าสู่ช่วงผ่อนผัน */
    List<PawnTicket> promoteOverdueToGrace(LocalDate asOf);

    /** งานประจำวัน: ทำให้ตั๋วที่พ้นผ่อนผันหลุดจำนำ */
    List<PawnTicket> forfeitExpired(LocalDate asOf);
}
