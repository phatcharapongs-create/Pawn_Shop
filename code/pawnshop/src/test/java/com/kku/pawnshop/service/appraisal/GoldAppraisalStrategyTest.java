package com.kku.pawnshop.service.appraisal;

import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.enums.ItemType;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import com.kku.pawnshop.service.pricing.GoldPriceProvider;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoldAppraisalStrategyTest {
    @Test void calculatesValueFromWeightPriceAndPurity() {
        GoldPriceProvider prices = mock(GoldPriceProvider.class);
        when(prices.pricePerGram(any(LocalDate.class))).thenReturn(Money.of(2500));
        GoldAppraisalStrategy strategy = new GoldAppraisalStrategy(prices);
        PledgedItem item = new PledgedItem(); item.setItemType(ItemType.GOLD);
        item.setWeightGram(new BigDecimal("10")); item.setPurityPercent(new BigDecimal("96.5"));
        assertEquals(Money.of(new BigDecimal("24125")), strategy.appraise(item));
        assertEquals(new BigDecimal("0.80"), strategy.loanToValueRatio());
    }
    @Test void rejectsMissingGoldMeasurements() {
        GoldAppraisalStrategy strategy = new GoldAppraisalStrategy(date -> Money.of(2500));
        PledgedItem item = new PledgedItem(); item.setItemType(ItemType.GOLD);
        assertThrows(BusinessRuleViolationException.class, () -> strategy.appraise(item));
    }
}

