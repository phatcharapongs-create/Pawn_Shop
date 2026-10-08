package com.kku.pawnshop.domain.entity;

import com.kku.pawnshop.domain.enums.LedgerEntryType;
import com.kku.pawnshop.domain.vo.Money;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", updatable = false)
    private PawnTicket ticket;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", updatable = false)
    private LedgerEntryType entryType;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "principal_amount", updatable = false))
    })
    private Money principalAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "interest_amount", updatable = false))
    })
    private Money interestAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount", updatable = false))
    })
    private Money totalAmount;

    @Column(name = "interest_from", updatable = false)
    private LocalDate interestFrom;

    @Column(name = "interest_to", updatable = false)
    private LocalDate interestTo;

    @Column(name = "entry_date", updatable = false)
    private LocalDate entryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by", updatable = false)
    private Employee handledBy;

    @Column(updatable = false)
    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 1. Default Constructor สำหรับให้ Spring Boot ใช้งาน
    protected LedgerEntry() {}

    // 2. Constructor สำหรับบันทึกรายการบัญชี (ตรงกับที่คุณเรียกใช้ใน LedgerServiceImpl)
    public LedgerEntry(PawnTicket ticket, LedgerEntryType entryType, Money principalAmount, 
                       Money interestAmount, Money totalAmount, LocalDate interestFrom, 
                       LocalDate interestTo, LocalDate entryDate, Employee handledBy, String note) {
        this.ticket = ticket;
        this.entryType = entryType;
        this.principalAmount = principalAmount;
        this.interestAmount = interestAmount;
        this.totalAmount = totalAmount;
        this.interestFrom = interestFrom;
        this.interestTo = interestTo;
        this.entryDate = entryDate;
        this.handledBy = handledBy;
        this.note = note;
        this.createdAt = LocalDateTime.now();
    }

    // ==========================================
    // เขียน GETTER เองทั้งหมด (ไม่ง้อ Lombok)
    // ==========================================
    public Long getId() { return id; }
    public PawnTicket getTicket() { return ticket; }
    public LedgerEntryType getEntryType() { return entryType; }
    public Money getPrincipalAmount() { return principalAmount; }
    public Money getInterestAmount() { return interestAmount; }
    public Money getTotalAmount() { return totalAmount; }
    public LocalDate getInterestFrom() { return interestFrom; }
    public LocalDate getInterestTo() { return interestTo; }
    public LocalDate getEntryDate() { return entryDate; }
    public Employee getHandledBy() { return handledBy; }
    public String getNote() { return note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}