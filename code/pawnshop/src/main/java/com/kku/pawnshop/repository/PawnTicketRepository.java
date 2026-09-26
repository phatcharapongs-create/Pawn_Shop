package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** เจ้าของ: สมาชิก C (พีท) */
@Repository
public interface PawnTicketRepository extends JpaRepository<PawnTicket, Long> {

    Optional<PawnTicket> findByTicketNumber(String ticketNumber);

    Page<PawnTicket> findByCustomerId(Long customerId, Pageable pageable);

    Page<PawnTicket> findByStatus(TicketStatus status, Pageable pageable);

    /** ตั๋วที่เลยกำหนดแล้วแต่สถานะยังไม่อัปเดต ใช้โดย job ประจำวัน */
    List<PawnTicket> findByStatusAndDueDateBefore(TicketStatus status, LocalDate date);

    /** ตั๋วที่พ้นช่วงผ่อนผันแล้ว ต้องเปลี่ยนเป็นหลุดจำนำ */
    List<PawnTicket> findByStatusAndGraceEndDateBefore(TicketStatus status, LocalDate date);

    /** ตั๋วที่ใกล้ครบกำหนด ใช้ส่งแจ้งเตือน */
    List<PawnTicket> findByStatusAndDueDateBetween(TicketStatus status, LocalDate from, LocalDate to);

    long countByStatus(TicketStatus status);
}
