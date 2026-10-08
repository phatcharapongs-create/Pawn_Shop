package com.kku.pawnshop.service.interest;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class HalfMonthFractionRule implements MonthFractionRule {

    @Override
    public BigDecimal chargeableMonths(LocalDate from, LocalDate to) {
        if (from == null || to == null || to.isBefore(from)) {
            return BigDecimal.ZERO;
        }

        // 1. หาจำนวนเดือนเต็ม
        long fullMonths = ChronoUnit.MONTHS.between(from, to);
        
        // 2. หาวันที่เหลือเศษหลังจากหักเดือนเต็มออกไปแล้ว
        LocalDate dateAfterFullMonths = from.plusMonths(fullMonths);
        long remainingDays = ChronoUnit.DAYS.between(dateAfterFullMonths, to);

        BigDecimal totalMonths = BigDecimal.valueOf(fullMonths);

        // 3. กฎการปัดเศษตามธรรมเนียมโรงรับจำนำ
        if (fullMonths == 0 && remainingDays == 0) {
            // จำนำและไถ่ถอนในวันเดียวกัน คิดขั้นต่ำครึ่งเดือน
            return new BigDecimal("0.5"); 
        } else if (remainingDays > 0 && remainingDays <= 15) {
            // เศษไม่เกิน 15 วัน ปัดเป็นครึ่งเดือน
            return totalMonths.add(new BigDecimal("0.5"));
        } else if (remainingDays > 15) {
            // เศษเกิน 15 วัน ปัดเป็น 1 เดือนเต็ม
            return totalMonths.add(BigDecimal.ONE);
        }

        // กรณีลงตัวพอดีเดือน (remainingDays == 0)
        return totalMonths;
    }
}