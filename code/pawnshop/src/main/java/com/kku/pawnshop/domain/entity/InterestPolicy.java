package com.kku.pawnshop.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * นโยบายอัตราดอกเบี้ยที่บังคับใช้ในช่วงเวลาหนึ่ง — เจ้าของ: สมาชิก D
 *
 * เหตุผลที่แยกเป็นตารางแทนการฮาร์ดโค้ด:
 * กฎหมายแก้อัตราได้ และเมื่อแก้แล้ว ตั๋วที่ออกก่อนหน้าต้องคิดด้วยอัตราเดิม
 * ตั๋วแต่ละใบจึงผูกกับ policy ณ วันที่ออกตั๋ว ไม่ใช่ policy ปัจจุบัน
 * การเพิ่มอัตราใหม่จึงไม่ต้องแก้โค้ดแม้แต่บรรทัดเดียว (OCP ระดับ data model)
 */
@Entity
@Table(name = "interest_policy")
@Getter
@Setter
@NoArgsConstructor
public class InterestPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 30, unique = true)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    /** null = ยังบังคับใช้อยู่ */
    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    /** จำนวนเดือนที่มีสิทธิ์ไถ่ถอนตามปกติ */
    @Column(name = "redemption_months", nullable = false)
    private int redemptionMonths;

    /** จำนวนวันผ่อนผันหลังครบกำหนด ก่อนทรัพย์หลุดจำนำ */
    @Column(name = "grace_days", nullable = false)
    private int graceDays;

    public boolean isEffectiveOn(LocalDate date) {
        boolean started = !date.isBefore(effectiveFrom);
        boolean notEnded = (effectiveTo == null) || !date.isAfter(effectiveTo);
        return started && notEnded;
    }
}
