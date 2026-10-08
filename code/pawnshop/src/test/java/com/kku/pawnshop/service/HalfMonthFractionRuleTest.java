package com.kku.pawnshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HalfMonthFractionRuleTest {

    private HalfMonthFractionRule rule;

    @BeforeEach
    void setUp() {
        // สร้าง Object ของ Rule ก่อนเริ่มเทสต์แต่ละข้อ
        rule = new HalfMonthFractionRule();
    }

    @Test
    @DisplayName("จำนำและไถ่ถอนในวันเดียวกัน (0 วัน) ต้องคิดดอกเบี้ยขั้นต่ำ ครึ่งเดือน (0.5)")
    void testSameDayRedemption() {
        LocalDate startDate = LocalDate.of(2023, 10, 1);
        LocalDate endDate = LocalDate.of(2023, 10, 1);

        BigDecimal fraction = rule.calculateMonthFraction(startDate, endDate);

        assertEquals(new BigDecimal("0.5"), fraction);
    }

    @Test
    @DisplayName("ไถ่ถอนภายใน 15 วัน ต้องปัดเป็น ครึ่งเดือน (0.5)")
    void testWithin15Days() {
        LocalDate startDate = LocalDate.of(2023, 10, 1);
        LocalDate endDate = LocalDate.of(2023, 10, 15); // ห่างกัน 14 วัน

        BigDecimal fraction = rule.calculateMonthFraction(startDate, endDate);

        assertEquals(new BigDecimal("0.5"), fraction);
    }

    @Test
    @DisplayName("ไถ่ถอนเกิน 15 วัน แต่ไม่เกิน 1 เดือน ต้องปัดเป็น 1 เดือนเต็ม (1.0)")
    void testOver15Days() {
        LocalDate startDate = LocalDate.of(2023, 10, 1);
        LocalDate endDate = LocalDate.of(2023, 10, 17); // ห่างกัน 16 วัน

        BigDecimal fraction = rule.calculateMonthFraction(startDate, endDate);

        assertEquals(new BigDecimal("1.0"), fraction);
    }

    @Test
    @DisplayName("จำนำข้ามเดือน: 1 เดือนกับอีก 10 วัน ต้องปัดเป็น 1.5 เดือน")
    void testOverOneMonthAndWithin15Days() {
        LocalDate startDate = LocalDate.of(2023, 10, 1);
        LocalDate endDate = LocalDate.of(2023, 11, 10); 

        BigDecimal fraction = rule.calculateMonthFraction(startDate, endDate);

        assertEquals(new BigDecimal("1.5"), fraction);
    }
}