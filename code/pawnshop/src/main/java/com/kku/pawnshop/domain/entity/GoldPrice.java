package com.kku.pawnshop.domain.entity;

import com.kku.pawnshop.domain.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * ราคาทองประจำวัน บันทึกโดยผู้จัดการ — เจ้าของ: สมาชิก B
 *
 * เก็บเป็นตารางแทนการเรียก API ภายนอก เพื่อไม่ให้ระบบล่มตอนนำเสนอ
 * ถ้าจะต่อ API จริงในอนาคต แค่เขียน GoldPriceProvider ตัวใหม่ (OCP)
 */
@Entity
@Table(name = "gold_price", indexes = {
        @Index(name = "idx_gold_price_date", columnList = "price_date", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class GoldPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price_date", nullable = false, unique = true)
    private LocalDate priceDate;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price_per_gram", nullable = false, precision = 15, scale = 2))
    private Money pricePerGram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by")
    private Employee recordedBy;
}
