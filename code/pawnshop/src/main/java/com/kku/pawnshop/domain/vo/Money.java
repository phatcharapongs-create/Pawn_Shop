package com.kku.pawnshop.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object แทนจำนวนเงิน
 *
 * เหตุผลที่ต้องมีคลาสนี้แทนการใช้ double หรือ BigDecimal ตรง ๆ:
 * 1. double มีปัญหาปัดเศษ ห้ามใช้กับเงินเด็ดขาด
 * 2. บังคับให้ทุกการคูณต้องระบุวิธีปัดเศษ ไม่ปล่อยให้แต่ละคนปัดกันคนละแบบ
 * 3. เป็น immutable ทุกการดำเนินการคืนอ็อบเจกต์ใหม่ ส่งต่อได้อย่างปลอดภัย
 */
@Embeddable
public class Money {

    public static final int SCALE = 2;

    @Column(name = "amount", nullable = false, precision = 15, scale = SCALE)
    private BigDecimal amount;

    protected Money() {
        // JPA ต้องการ no-arg constructor
    }

    private Money(BigDecimal amount) {
        this.amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount ต้องไม่เป็น null");
        return new Money(amount);
    }

    public static Money of(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Money plus(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money minus(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    /** คูณด้วยอัตราส่วน เช่น เปอร์เซ็นต์ดอกเบี้ย หรือจำนวนเดือน */
    public Money multiply(BigDecimal factor) {
        return new Money(this.amount.multiply(factor));
    }

    /** คูณด้วยเปอร์เซ็นต์ เช่น percent = 1.25 หมายถึง 1.25% */
    public Money percentage(BigDecimal percent) {
        return multiply(percent.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
    }

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isNegative() {
        return this.amount.signum() < 0;
    }

    public boolean isZero() {
        return this.amount.signum() == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money other)) return false;
        return amount.compareTo(other.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
