package com.kku.pawnshop.service.impl;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kku.pawnshop.domain.entity.Employee;
import com.kku.pawnshop.domain.entity.GoldPrice;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.dto.response.GoldPriceResponse;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import com.kku.pawnshop.repository.EmployeeRepository;
import com.kku.pawnshop.repository.GoldPriceRepository;
import com.kku.pawnshop.service.GoldPriceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoldPriceServiceImpl implements GoldPriceService {

    private final GoldPriceRepository goldPriceRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<GoldPriceResponse> findLatest() {
        PageRequest latestOnly = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "priceDate"));
        return goldPriceRepository.findAll(latestOnly).getContent().stream()
                .findFirst()
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GoldPriceResponse> findAll(Pageable pageable) {
        return goldPriceRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public GoldPriceResponse recordPrice(LocalDate priceDate, Money pricePerGram, Long employeeId) {
        if (priceDate == null) {
            throw new BusinessRuleViolationException("ต้องระบุวันที่ของราคาทอง");
        }
        if (pricePerGram == null || !pricePerGram.isGreaterThan(Money.zero())) {
            throw new BusinessRuleViolationException("ราคาทองต้องมากกว่า 0");
        }

        Employee employee = null;
        if (employeeId != null) {
            employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("พนักงาน", employeeId));
        }

        // 1 วันมีได้ 1 ราคา (unique ที่ price_date) ถ้ามีอยู่แล้วให้แก้ราคาเดิมแทนการเพิ่มใหม่
        GoldPrice price = goldPriceRepository.findByPriceDate(priceDate).orElseGet(GoldPrice::new);
        price.setPriceDate(priceDate);
        price.setPricePerGram(pricePerGram);
        price.setRecordedBy(employee);

        return toResponse(goldPriceRepository.save(price));
    }

    private GoldPriceResponse toResponse(GoldPrice price) {
        Employee by = price.getRecordedBy();
        String name = by != null ? by.getFirstName() + " " + by.getLastName() : null;
        return new GoldPriceResponse(price.getPriceDate(), price.getPricePerGram().getAmount(), name);
    }
}
