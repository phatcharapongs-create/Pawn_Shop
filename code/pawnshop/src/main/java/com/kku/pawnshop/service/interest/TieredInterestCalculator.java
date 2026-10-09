package com.kku.pawnshop.service.interest;

import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.entity.RateTier;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.repository.InterestPolicyRepository;
import com.kku.pawnshop.repository.RateTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TieredInterestCalculator implements InterestCalculator {

    private final MonthFractionRule monthFractionRule;
    private final InterestPolicyRepository policyRepository;
    private final RateTierRepository rateTierRepository;

    @Override
    public Money accruedInterest(PawnTicket ticket, LocalDate asOf) {
        // 1. วันที่เริ่มคิดดอกเบี้ย
        LocalDate fromDate = ticket.getInterestPaidUntil() != null 
                ? ticket.getInterestPaidUntil() : ticket.getPawnDate();

        // 2. หาจำนวนเดือนที่ต้องนำมาคิดดอกเบี้ย
        BigDecimal months = monthFractionRule.chargeableMonths(fromDate, asOf);
        if (months.compareTo(BigDecimal.ZERO) <= 0) {
            return Money.zero();
        }

        // 3. ดึงนโยบายดอกเบี้ย (แก้เป็น findEffectiveOn ให้ตรงกับ Repository)
        var policy = policyRepository.findEffectiveOn(ticket.getPawnDate())
                .orElseThrow(() -> new IllegalStateException("ไม่พบนโยบายดอกเบี้ยในระบบสำหรับวันที่: " + ticket.getPawnDate()));

        // 4. ดึงขั้นอัตราดอกเบี้ยทั้งหมดของนโยบายนั้น แล้วเรียงตามลำดับ
        List<RateTier> tiers = rateTierRepository.findByPolicyIdOrderByTierOrderAsc(policy.getId());

        // 5. คำนวณดอกเบี้ยแบบขั้นบันได
        BigDecimal remainingPrincipal = ticket.getPrincipal().getAmount();
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (RateTier tier : tiers) {
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
                break; 
            }

            BigDecimal lower = tier.getLowerBound().getAmount();
            BigDecimal upper = tier.getUpperBound() != null 
                    ? tier.getUpperBound().getAmount() 
                    : remainingPrincipal.add(lower); 

            BigDecimal tierCapacity = upper.subtract(lower);
            BigDecimal amountInThisTier = remainingPrincipal.min(tierCapacity);

            BigDecimal ratePerMonth = tier.getMonthlyRatePercent().divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
            BigDecimal interestForThisTier = amountInThisTier.multiply(ratePerMonth).multiply(months);

            totalInterest = totalInterest.add(interestForThisTier);
            remainingPrincipal = remainingPrincipal.subtract(amountInThisTier);
        }

        return Money.of(totalInterest.setScale(2, RoundingMode.HALF_UP));
    }
}