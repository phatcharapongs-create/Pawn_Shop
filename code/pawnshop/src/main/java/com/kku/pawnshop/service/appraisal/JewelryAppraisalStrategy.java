package com.kku.pawnshop.service.appraisal;

import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.enums.ItemType;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/** ประเมินนาฬิกาและเครื่องประดับจากราคาอ้างอิงของรุ่น ปรับตามสภาพ */
@Component
public class JewelryAppraisalStrategy implements AppraisalStrategy {
    private static final BigDecimal LTV = new BigDecimal("0.60");
    public boolean supports(ItemType type) { return type == ItemType.JEWELRY; }
    public Money appraise(PledgedItem item) {
        if (item.getReferencePrice() == null || item.getReferencePrice().isNegative() || item.getReferencePrice().isZero())
            throw new BusinessRuleViolationException("นาฬิกาและเครื่องประดับต้องระบุราคาอ้างอิงรุ่นมากกว่า 0");
        int grade = item.getConditionGrade() == null ? 3 : item.getConditionGrade();
        if (grade < 1 || grade > 5) throw new BusinessRuleViolationException("เกรดสภาพต้องอยู่ระหว่าง 1 ถึง 5");
        return item.getReferencePrice().multiply(BigDecimal.valueOf(0.6 + grade * 0.1));
    }
    public BigDecimal loanToValueRatio() { return LTV; }
}
