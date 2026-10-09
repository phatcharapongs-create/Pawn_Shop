package com.kku.pawnshop.service.interest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kku.pawnshop.domain.entity.InterestPolicy;
import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.entity.RateTier;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.repository.InterestPolicyRepository;
import com.kku.pawnshop.repository.RateTierRepository;

@ExtendWith(MockitoExtension.class)
public class TieredInterestCalculatorTest {

    @Mock
    private MonthFractionRule monthFractionRule;

    @Mock
    private InterestPolicyRepository policyRepository;

    @Mock
    private RateTierRepository rateTierRepository;

    @InjectMocks
    private TieredInterestCalculator calculator;

    @Mock
    private PawnTicket ticket;

    @Mock
    private InterestPolicy policy;

    @Mock
    private RateTier tier1;

    @Mock
    private RateTier tier2;

    @Test
    @DisplayName("ทดสอบการคำนวณดอกเบี้ยแบบขั้นบันได 2 ขั้น (เงินต้น 3,000 บาท)")
    void testAccruedInterest() {
        // 1. จำลองข้อมูลวันที่
        LocalDate pawnDate = LocalDate.of(2023, 1, 1);
        LocalDate asOf = LocalDate.of(2023, 2, 1);
        when(ticket.getPawnDate()).thenReturn(pawnDate);
        when(ticket.getInterestPaidUntil()).thenReturn(null);

        // 2. จำลองข้อมูลเงินต้น 3,000 บาท
        when(ticket.getPrincipal()).thenReturn(Money.of(new BigDecimal("3000")));

        // 3. จำลองจำนวนเดือน (สมมติว่าครบ 1 เดือนพอดี)
        when(monthFractionRule.chargeableMonths(pawnDate, asOf)).thenReturn(BigDecimal.ONE);

        // 4. จำลองนโยบายดอกเบี้ย (ใช้ findEffectiveOn ตาม Repository จริง)
        when(policy.getId()).thenReturn(1L);
        when(policyRepository.findEffectiveOn(pawnDate)).thenReturn(Optional.of(policy));

        // 5. จำลองอัตราดอกเบี้ย 2 ขั้น
        when(tier1.getLowerBound()).thenReturn(Money.zero());
        when(tier1.getUpperBound()).thenReturn(Money.of(new BigDecimal("2000")));
        when(tier1.getMonthlyRatePercent()).thenReturn(new BigDecimal("2.00"));

        when(tier2.getLowerBound()).thenReturn(Money.of(new BigDecimal("2000")));
        when(tier2.getUpperBound()).thenReturn(null);
        when(tier2.getMonthlyRatePercent()).thenReturn(new BigDecimal("1.25"));

        when(rateTierRepository.findByPolicyIdOrderByTierOrderAsc(1L)).thenReturn(Arrays.asList(tier1, tier2));

        // 6. เรียกใช้งานการคำนวณจริง
        Money interest = calculator.accruedInterest(ticket, asOf);

        // 7. ตรวจสอบผลลัพธ์
        assertEquals(new BigDecimal("52.50"), interest.getAmount(), "ดอกเบี้ยรวมต้องเท่ากับ 52.50 บาท");
    }
}