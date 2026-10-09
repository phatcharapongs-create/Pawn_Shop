package com.kku.pawnshop.service.interest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HalfMonthFractionRuleTest {

    private HalfMonthFractionRule rule;

    @BeforeEach
    void setUp() {
        rule = new HalfMonthFractionRule();
    }

    @Test
    @DisplayName("ไถ่ถอนวันเดียวกัน คิดเป็นครึ่งเดือน (0.5)")
    void testSameDayRedemption() {
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 1);
        assertEquals(new BigDecimal("0.5"), rule.chargeableMonths(from, to));
    }

    @Test
    @DisplayName("ไม่เกิน 15 วัน คิดเป็นครึ่งเดือน (0.5)")
    void testWithin15Days() {
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 15);
        assertEquals(new BigDecimal("0.5"), rule.chargeableMonths(from, to));
    }

    @Test
    @DisplayName("เกิน 15 วัน คิดเป็น 1 เดือน (1.0)")
    void testOver15Days() {
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 16);
        assertEquals(new BigDecimal("1.0"), rule.chargeableMonths(from, to));
    }

    @Test
    @DisplayName("1 เดือนกับอีกไม่เกิน 15 วัน คิดเป็น 1.5 เดือน")
    void testOverOneMonthAndWithin15Days() {
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 2, 10);
        assertEquals(new BigDecimal("1.5"), rule.chargeableMonths(from, to));
    }
}