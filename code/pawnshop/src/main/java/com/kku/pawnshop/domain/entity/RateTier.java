package com.kku.pawnshop.domain.entity;

import com.kku.pawnshop.domain.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * ขั้นอัตราดอกเบี้ย — เจ้าของ: สมาชิก D
 *
 * ความสัมพันธ์ One-to-Many จาก InterestPolicy
 *
 * คิดแบบขั้นบันไดเหมือนภาษีเงินได้ ไม่ใช่ใช้อัตราเดียวทั้งก้อน
 * เช่น เงินต้น 5,000 ที่มี 2 ขั้น จะคิด 2,000 แรกด้วยอัตราขั้นที่ 1
 * และอีก 3,000 ที่เหลือด้วยอัตราขั้นที่ 2
 *
 * *** ตัวเลขอัตราจริงต้องไปตรวจสอบกับ พ.ร.บ.โรงรับจำนำ ฉบับปัจจุบัน
 *     ก่อนใส่ลง data.sql และอ้างอิงไว้ในรายงาน ***
 */
@Entity
@Table(name = "rate_tier", indexes = {
        @Index(name = "idx_rate_tier_policy", columnList = "policy_id")
})
@Getter
@Setter
@NoArgsConstructor
public class RateTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private InterestPolicy policy;

    /** ขอบล่างของขั้น (รวมค่านี้) */
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "lower_bound", nullable = false, precision = 15, scale = 2))
    private Money lowerBound;

    /** ขอบบนของขั้น (รวมค่านี้) null = ไม่จำกัด */
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "upper_bound", precision = 15, scale = 2))
    private Money upperBound;

    /** อัตราต่อเดือนเป็นเปอร์เซ็นต์ เช่น 1.25 หมายถึง 1.25% ต่อเดือน */
    @Column(name = "monthly_rate_percent", nullable = false, precision = 6, scale = 4)
    private BigDecimal monthlyRatePercent;

    @Column(name = "tier_order", nullable = false)
    private int tierOrder;
}
