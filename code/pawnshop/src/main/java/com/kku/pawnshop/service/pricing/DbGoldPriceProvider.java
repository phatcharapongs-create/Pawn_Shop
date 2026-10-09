package com.kku.pawnshop.service.pricing;

import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import com.kku.pawnshop.repository.GoldPriceRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

/** อ่านราคาทองจากฐานข้อมูล โดยใช้ราคาล่าสุดที่ไม่เกินวันที่ขอ */
@Component
public class DbGoldPriceProvider implements GoldPriceProvider {
    private final GoldPriceRepository repository;
    public DbGoldPriceProvider(GoldPriceRepository repository) { this.repository = repository; }
    @Override public Money pricePerGram(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("date must not be null");
        return repository.findTopByPriceDateLessThanEqualOrderByPriceDateDesc(date)
                .map(price -> price.getPricePerGram())
                .orElseThrow(() -> new BusinessRuleViolationException("ไม่พบราคาทองที่บันทึกไว้ ณ วันที่ " + date));
    }
}
