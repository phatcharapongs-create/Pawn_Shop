package com.kku.pawnshop.service.appraisal;

import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.enums.ItemType;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;
import java.time.Year;
import static org.junit.jupiter.api.Assertions.*;

class ElectronicsAppraisalStrategyTest {
    @Test void appliesAgeDepreciationAndConditionFactor() {
        ElectronicsAppraisalStrategy strategy = new ElectronicsAppraisalStrategy();
        PledgedItem item = new PledgedItem(); item.setItemType(ItemType.ELECTRONICS);
        item.setReferencePrice(Money.of(10000)); item.setManufactureYear(Year.now().getValue() - 2); item.setConditionGrade(3);
        assertEquals(Money.of(9000), strategy.appraise(item));
        assertEquals(new java.math.BigDecimal("0.50"), strategy.loanToValueRatio());
    }
    @Test void rejectsMissingReferencePrice() {
        PledgedItem item = new PledgedItem(); item.setItemType(ItemType.ELECTRONICS);
        assertThrows(BusinessRuleViolationException.class, () -> new ElectronicsAppraisalStrategy().appraise(item));
    }
}

