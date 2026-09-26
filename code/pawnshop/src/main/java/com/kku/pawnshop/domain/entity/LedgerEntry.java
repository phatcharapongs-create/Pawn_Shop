package com.kku.pawnshop.domain.entity;

import com.kku.pawnshop.domain.enums.LedgerEntryType;
import com.kku.pawnshop.domain.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * รายการธุรกรรมของตั๋ว — เจ้าของ: สมาชิก D
 *
 * ตารางนี้เป็น append-only ห้าม UPDATE และห้าม DELETE เด็ดขาด
 * ถ้าบันทึกผิดให้ออกรายการกลับรายการใหม่ ไม่ใช่ไปแก้ของเดิม
 *
 * เหตุผล: ระบบการเงินต้องตรวจสอบย้อนหลังได้ว่ายอดปัจจุบันมาจากไหน
 * ถ้าเก็บยอดคงเหลือเป็นฟิลด์ใน PawnTicket แล้ว UPDATE ทับไปเรื่อย ๆ
 * เมื่อยอดผิดจะไม่มีทางรู้เลยว่าผิดตั้งแต่ธุรกรรมไหน
 */
@Entity
@Table(name = "ledger_entry", indexes = {
        @Index(name = "idx_ledger_ticket", columnList = "ticket_id"),
        @Index(name = "idx_ledger_date", columnList = "entry_date")
})
@Getter
@Setter
@NoArgsConstructor
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private PawnTicket ticket;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 30)
    private LedgerEntryType entryType;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "principal_amount", nullable = false, precision = 15, scale = 2))
    private Money principalAmount;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "interest_amount", nullable = false, precision = 15, scale = 2))
    private Money interestAmount;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "total_amount", nullable = false, precision = 15, scale = 2))
    private Money totalAmount;

    /** ช่วงเวลาที่ดอกเบี้ยรายการนี้ครอบคลุม ใช้ตรวจสอบย้อนหลัง */
    @Column(name = "interest_from")
    private LocalDate interestFrom;

    @Column(name = "interest_to")
    private LocalDate interestTo;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by")
    private Employee handledBy;

    @Column(name = "note", length = 300)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
