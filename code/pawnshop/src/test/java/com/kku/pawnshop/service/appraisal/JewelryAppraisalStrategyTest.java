package com.kku.pawnshop.service.appraisal;

import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.enums.ItemType;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JewelryAppraisalStrategyTest {
    @Test void usesReferenceModelPriceAndCondition() {
        JewelryAppraisalStrategy strategy = new JewelryAppraisalStrategy();
        PledgedItem item = new PledgedItem(); item.setItemType(ItemType.JEWELRY);
        item.setReferencePrice(Money.of(10000)); item.setConditionGrade(5);
        assertEquals(Money.of(11000), strategy.appraise(item));
        assertEquals(new java.math.BigDecimal("0.60"), strategy.loanToValueRatio());
    }
    @Test void rejectsMissingReferencePrice() {
        PledgedItem item = new PledgedItem(); item.setItemType(ItemType.JEWELRY);
        assertThrows(BusinessRuleViolationException.class, () -> new JewelryAppraisalStrategy().appraise(item));
    }
}

