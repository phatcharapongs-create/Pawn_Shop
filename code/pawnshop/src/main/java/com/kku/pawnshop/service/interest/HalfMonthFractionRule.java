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
            return new BigDecimal("0.0");
        }

        long fullMonths = ChronoUnit.MONTHS.between(from, to);
        
        LocalDate dateAfterFullMonths = from.plusMonths(fullMonths);
        long remainingDays = ChronoUnit.DAYS.between(dateAfterFullMonths, to);

        // บังคับให้เดือนเต็มมีทศนิยม 1 ตำแหน่ง (เช่น 1 -> 1.0)
        BigDecimal totalMonths = BigDecimal.valueOf(fullMonths).setScale(1);

        if (fullMonths == 0 && remainingDays == 0) {
            return new BigDecimal("0.5"); 
        } else if (remainingDays > 0 && remainingDays <= 14) { 
            return totalMonths.add(new BigDecimal("0.5"));
        } else if (remainingDays > 14) {
            // แก้จาก BigDecimal.ONE เป็น "1.0"
            return totalMonths.add(new BigDecimal("1.0")); 
        }

        return totalMonths;
    }

    public BigDecimal calculateMonthFraction(LocalDate from, LocalDate to) {
        return chargeableMonths(from, to);
    }
}