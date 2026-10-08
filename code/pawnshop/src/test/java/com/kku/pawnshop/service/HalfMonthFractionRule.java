package com.kku.pawnshop.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Component
public class HalfMonthFractionRule {

    public BigDecimal calculateMonthFraction(LocalDate startDate, LocalDate endDate) {
        // กรณีไถ่ถอนวันเดียวกัน คิดดอกเบี้ยขั้นต่ำ 15 วัน (0.5 เดือน)
        if (startDate.isEqual(endDate)) {
            return new BigDecimal("0.5");
        }

        // คำนวณระยะห่างว่ากี่เดือน กี่วัน
        Period period = Period.between(startDate, endDate);
        int months = period.getMonths() + (period.getYears() * 12);
        int days = period.getDays();

        BigDecimal totalMonths = new BigDecimal(months);

        // กฎการปัดเศษวันตาม พ.ร.บ. โรงรับจำนำ
        if (days > 0 && days <= 15) {
            // เศษวันไม่เกิน 15 วัน ปัดเป็นครึ่งเดือน
            totalMonths = totalMonths.add(new BigDecimal("0.5"));
        } else if (days > 15) {
            // เศษวันเกิน 15 วัน ปัดเป็น 1 เดือนเต็ม
            totalMonths = totalMonths.add(new BigDecimal("1.0"));
        }

        // ป้องกันกรณีคำนวณแล้วได้ 0 เดือน (เช่น บั๊กวันที่) ให้คิดขั้นต่ำ 0.5
        if (totalMonths.compareTo(BigDecimal.ZERO) == 0) {
            return new BigDecimal("0.5");
        }

        return totalMonths;
    }
}