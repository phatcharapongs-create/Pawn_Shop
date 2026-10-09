package com.kku.pawnshop.service.interest;

import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.vo.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DefaultInterestCalculator implements InterestCalculator {

    @Override
    public Money accruedInterest(PawnTicket ticket, LocalDate asOf) {
        return Money.of(BigDecimal.ZERO);
    }
}