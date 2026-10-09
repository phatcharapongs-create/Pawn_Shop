package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.Appraisal;
import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.enums.ItemType;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import com.kku.pawnshop.repository.AppraisalRepository;
import com.kku.pawnshop.repository.EmployeeRepository;
import com.kku.pawnshop.repository.PledgedItemRepository;
import com.kku.pawnshop.service.appraisal.AppraisalStrategy;
import com.kku.pawnshop.service.appraisal.AppraisalStrategyResolver;
import com.kku.pawnshop.service.pricing.GoldPriceProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class AppraisalServiceImpl implements AppraisalService {
    private final AppraisalRepository appraisalRepository;
    private final PledgedItemRepository pledgedItemRepository;
    private final EmployeeRepository employeeRepository;
    private final AppraisalStrategyResolver strategyResolver;
    private final GoldPriceProvider goldPriceProvider;

    public AppraisalServiceImpl(AppraisalRepository appraisalRepository, PledgedItemRepository pledgedItemRepository,
            EmployeeRepository employeeRepository, AppraisalStrategyResolver strategyResolver,
            GoldPriceProvider goldPriceProvider) {
        this.appraisalRepository = appraisalRepository;
        this.pledgedItemRepository = pledgedItemRepository;
        this.employeeRepository = employeeRepository;
        this.strategyResolver = strategyResolver;
        this.goldPriceProvider = goldPriceProvider;
    }

    @Override
    public Appraisal appraise(PledgedItem item, Long appraiserId) {
        if (item == null || item.getItemType() == null) {
            throw new BusinessRuleViolationException("กรุณาระบุทรัพย์และประเภททรัพย์");
        }
        PledgedItem persisted = item.getId() == null ? pledgedItemRepository.save(item)
                : pledgedItemRepository.findById(item.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("ไม่พบทรัพย์รหัส " + item.getId()));
        if (appraisalRepository.findByPledgedItemId(persisted.getId()).isPresent()) {
            throw new BusinessRuleViolationException("ทรัพย์ชิ้นนี้มีผลประเมินแล้ว");
        }
        AppraisalStrategy strategy = strategyResolver.resolve(persisted.getItemType());
        Appraisal appraisal = new Appraisal();
        appraisal.setPledgedItem(persisted);
        if (appraiserId != null) {
            appraisal.setAppraiser(employeeRepository.findById(appraiserId)
                    .orElseThrow(() -> new ResourceNotFoundException("ไม่พบพนักงานผู้ประเมินรหัส " + appraiserId)));
        }
        appraisal.setAppraisedValue(strategy.appraise(persisted));
        appraisal.setMaxLoanAmount(strategy.maxLoanAmount(persisted));
        if (persisted.getItemType() == ItemType.GOLD) {
            appraisal.setGoldPriceSnapshot(goldPriceProvider.pricePerGram(LocalDate.now()));
        }
        appraisal.setAppraisedAt(LocalDateTime.now());
        return appraisalRepository.save(appraisal);
    }

    @Override
    @Transactional(readOnly = true)
    public Appraisal findByPledgedItemId(Long pledgedItemId) {
        return appraisalRepository.findByPledgedItemId(pledgedItemId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผลประเมินของทรัพย์รหัส " + pledgedItemId));
    }
}
