package com.kku.pawnshop.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.enums.TicketStatus;

/**
 * Query สำหรับหน้าทรัพย์หลุดจำนำ (อ่านอย่างเดียว)
 * แยกเป็นไฟล์ใหม่ เพื่อไม่ต้องแก้ PawnTicketRepository ของเจ้าของเดิม
 */
public interface ForfeitedItemQueryRepository extends Repository<PawnTicket, Long> {

    /** โหลด customer มาพร้อมกัน กัน N+1 ตอนแสดงชื่อลูกค้าในตาราง */
    @EntityGraph(attributePaths = {"customer"})
    Page<PawnTicket> findByStatusIn(Collection<TicketStatus> statuses, Pageable pageable);
}
