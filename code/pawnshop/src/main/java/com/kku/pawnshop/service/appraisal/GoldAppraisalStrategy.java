package com.kku.pawnshop.service.appraisal;

import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.enums.ItemType;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import com.kku.pawnshop.service.pricing.GoldPriceProvider;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;

/** ประเมินทองจากน้ำหนัก ราคาทองล่าสุด และเปอร์เซ็นต์ความบริสุทธิ์ */
@Component
public class GoldAppraisalStrategy implements AppraisalStrategy {
    private static final BigDecimal LTV = new BigDecimal("0.80");
    private final GoldPriceProvider goldPriceProvider;
    public GoldAppraisalStrategy(GoldPriceProvider goldPriceProvider) { this.goldPriceProvider = goldPriceProvider; }
    @Override public boolean supports(ItemType type) { return type == ItemType.GOLD; }
    @Override public Money appraise(PledgedItem item) {
        if (item.getWeightGram() == null || item.getWeightGram().signum() <= 0 || item.getPurityPercent() == null
                || item.getPurityPercent().signum() <= 0 || item.getPurityPercent().compareTo(new BigDecimal("100")) > 0)
            throw new BusinessRuleViolationException("ทองต้องระบุน้ำหนักมากกว่า 0 และความบริสุทธิ์ระหว่าง 0 ถึง 100 เปอร์เซ็นต์");
        BigDecimal factor = item.getWeightGram().multiply(item.getPurityPercent()).movePointLeft(2);
        return goldPriceProvider.pricePerGram(LocalDate.now()).multiply(factor);
    }
    @Override public BigDecimal loanToValueRatio() { return LTV; }
}
