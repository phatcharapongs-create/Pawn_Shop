package com.kku.pawnshop.service.appraisal;

import com.kku.pawnshop.domain.enums.ItemType;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * เลือก AppraisalStrategy ที่เหมาะกับประเภททรัพย์
 *
 * จุดสำคัญ: ห้ามเขียนเป็น switch (type) { case GOLD: ... } เด็ดขาด
 * เพราะเพิ่มประเภทใหม่ทีไรต้องกลับมาแก้ตรงนี้ = ละเมิด OCP
 * ใช้ supports() ให้แต่ละ strategy ตอบเองว่ารับผิดชอบประเภทไหน
 */
@Component
public class AppraisalStrategyResolver {

    private final List<AppraisalStrategy> strategies;

    public AppraisalStrategyResolver(List<AppraisalStrategy> strategies) {
        this.strategies = strategies;
    }

    public AppraisalStrategy resolve(ItemType type) {
        return strategies.stream()
                .filter(s -> s.supports(type))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "ยังไม่รองรับการประเมินทรัพย์ประเภท " + type));
    }
}
