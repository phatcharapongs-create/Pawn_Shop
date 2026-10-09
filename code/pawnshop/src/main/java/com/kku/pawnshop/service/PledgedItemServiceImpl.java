package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import com.kku.pawnshop.repository.PledgedItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class PledgedItemServiceImpl implements PledgedItemService {
    private final PledgedItemRepository repository;
    public PledgedItemServiceImpl(PledgedItemRepository repository) { this.repository = repository; }

    @Override
    public PledgedItem register(PledgedItem item) {
        if (item == null || item.getItemType() == null || item.getDescription() == null || item.getDescription().isBlank()) {
            throw new BusinessRuleViolationException("กรุณาระบุประเภทและรายละเอียดทรัพย์");
        }
        if (item.getId() != null) {
            throw new BusinessRuleViolationException("การลงทะเบียนทรัพย์ใหม่ต้องไม่มีรหัสเดิม");
        }
        return repository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public PledgedItem findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ไม่พบทรัพย์รหัส " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PledgedItem> findForfeitedItemsForSale() { return repository.findForfeitedItemsForSale(); }
}
