package com.kku.pawnshop.domain.entity;

import com.kku.pawnshop.domain.enums.TicketStatus;
import com.kku.pawnshop.domain.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ตั๋วจำนำ หัวใจของระบบ — เจ้าของ: สมาชิก C (พีท)
 *
 * ความสัมพันธ์ One-to-One กับ PledgedItem (ตั๋ว 1 ใบต่อทรัพย์ 1 ชิ้น)
 * ความสัมพันธ์ One-to-Many ไป LedgerEntry แต่ทำเป็น unidirectional
 * จากฝั่ง LedgerEntry เท่านั้น เพื่อไม่ให้ JSON วนลูปและไม่โหลดรายการ
 * ธุรกรรมทั้งหมดโดยไม่จำเป็น ดึงผ่าน LedgerEntryRepository.findByTicketId แทน
 *
 * State Pattern: entity เก็บแค่ enum ส่วนพฤติกรรมของแต่ละสถานะ
 * อยู่ในคลาส TicketState ที่ TicketStateFactory เป็นคนจ่ายให้
 * entity จึงไม่มี if-else เช็คสถานะแม้แต่ที่เดียว
 */
@Entity
@Table(name = "pawn_ticket", indexes = {
        @Index(name = "idx_ticket_number", columnList = "ticket_number", unique = true),
        @Index(name = "idx_ticket_status", columnList = "status"),
        @Index(name = "idx_ticket_customer", columnList = "customer_id"),
        @Index(name = "idx_ticket_due_date", columnList = "due_date")
})
@Getter
@Setter
@NoArgsConstructor
public class PawnTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_number", nullable = false, length = 20, unique = true)
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /**
     * EAGER เพราะทุกหน้าจอที่แสดงตั๋วต้องแสดงทรัพย์ควบคู่เสมอ
     * ถ้าใช้ LAZY จะเกิด N+1 query ในหน้ารายการตั๋ว
     */
    @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pledged_item_id", nullable = false, unique = true)
    private PledgedItem pledgedItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interest_policy_id", nullable = false)
    private InterestPolicy interestPolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opened_by")
    private Employee openedBy;

    /** เงินต้นที่จ่ายให้ลูกค้า คงที่ตลอดอายุตั๋วในขอบเขตของโปรเจกต์นี้ */
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "principal", nullable = false, precision = 15, scale = 2))
    private Money principal;

    @Column(name = "pawn_date", nullable = false)
    private LocalDate pawnDate;

    /** วันครบกำหนดไถ่ถอน เลื่อนออกไปทุกครั้งที่ต่อดอก */
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /** วันสุดท้ายของช่วงผ่อนผัน พ้นวันนี้แล้วทรัพย์หลุดจำนำ */
    @Column(name = "grace_end_date", nullable = false)
    private LocalDate graceEndDate;

    /** วันที่คิดดอกเบี้ยครั้งล่าสุด ใช้เป็นจุดตั้งต้นคำนวณดอกค้าง */
    @Column(name = "interest_paid_until", nullable = false)
    private LocalDate interestPaidUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TicketStatus status = TicketStatus.ACTIVE;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "seized_reason", length = 300)
    private String seizedReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /** เลยกำหนดไถ่ถอนแล้วหรือยัง ณ วันที่กำหนด */
    public boolean isPastDue(LocalDate asOf) {
        return asOf.isAfter(dueDate);
    }

    /** พ้นช่วงผ่อนผันแล้วหรือยัง ถ้าใช่ถือว่าทรัพย์หลุดจำนำ */
    public boolean isPastGrace(LocalDate asOf) {
        return asOf.isAfter(graceEndDate);
    }
}
