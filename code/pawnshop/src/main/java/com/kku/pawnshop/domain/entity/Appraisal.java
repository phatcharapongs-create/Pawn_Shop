package com.kku.pawnshop.domain.entity;

import com.kku.pawnshop.domain.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * ผลการประเมินราคาทรัพย์ — เจ้าของ: สมาชิก B
 *
 * ความสัมพันธ์ One-to-One กับ PledgedItem (ทรัพย์ 1 ชิ้นประเมิน 1 ครั้งต่อการจำนำ)
 *
 * จุดสำคัญ: goldPricePerGramSnapshot เก็บราคาทอง ณ วันประเมิน
 * ห้ามไปดึงราคาวันนี้มาแสดงย้อนหลัง เพราะราคาทองเปลี่ยนทุกวัน
 * ตั๋วเก่าต้องอธิบายได้ว่าตอนนั้นตีราคาจากอะไร
 */
@Entity
@Table(name = "appraisal")
@Getter
@Setter
@NoArgsConstructor
public class Appraisal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pledged_item_id", nullable = false, unique = true)
    private PledgedItem pledgedItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appraiser_id")
    private Employee appraiser;

    /** มูลค่าตลาดที่ประเมินได้ */
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "appraised_value", nullable = false, precision = 15, scale = 2))
    private Money appraisedValue;

    /** วงเงินสูงสุดที่ให้จำนำได้ = appraisedValue x LTV ของประเภททรัพย์ */
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "max_loan_amount", nullable = false, precision = 15, scale = 2))
    private Money maxLoanAmount;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "gold_price_snapshot", precision = 15, scale = 2))
    private Money goldPriceSnapshot;

    @Column(name = "appraised_at", nullable = false)
    private LocalDateTime appraisedAt;

    @Column(name = "note", length = 500)
    private String note;

    @PrePersist
    void onCreate() {
        if (this.appraisedAt == null) {
            this.appraisedAt = LocalDateTime.now();
        }
    }
}
