package com.kku.pawnshop.mapper;

import com.kku.pawnshop.domain.entity.Appraisal;
import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.dto.pledged.AppraisalResponse;
import com.kku.pawnshop.dto.pledged.PledgedItemRequest;
import com.kku.pawnshop.dto.pledged.PledgedItemResponse;
import org.springframework.stereotype.Component;

@Component
public class PledgedItemMapper {
    public PledgedItem toEntity(PledgedItemRequest request) {
        PledgedItem item = new PledgedItem();
        item.setItemType(request.getItemType());
        item.setDescription(request.getDescription());
        item.setSerialNumber(request.getSerialNumber());
        item.setWeightGram(request.getWeightGram());
        item.setPurityPercent(request.getPurityPercent());
        item.setManufactureYear(request.getManufactureYear());
        item.setReferencePrice(request.getReferencePrice() == null ? null : Money.of(request.getReferencePrice()));
        item.setConditionGrade(request.getConditionGrade());
        item.setStorageSlot(request.getStorageSlot());
        item.setPhotoUrl(request.getPhotoUrl());
        return item;
    }
    public PledgedItemResponse toResponse(PledgedItem item) {
        return new PledgedItemResponse(item.getId(), item.getItemType(), item.getDescription(), item.getSerialNumber(),
                item.getWeightGram(), item.getPurityPercent(), item.getManufactureYear(),
                item.getReferencePrice() == null ? null : item.getReferencePrice().getAmount(),
                item.getConditionGrade(), item.getStorageSlot(), item.getPhotoUrl());
    }
    public AppraisalResponse toResponse(Appraisal appraisal) {
        return new AppraisalResponse(appraisal.getId(), appraisal.getPledgedItem().getId(),
                appraisal.getAppraisedValue().getAmount(), appraisal.getMaxLoanAmount().getAmount(),
                appraisal.getGoldPriceSnapshot() == null ? null : appraisal.getGoldPriceSnapshot().getAmount(),
                appraisal.getAppraisedAt(), appraisal.getNote());
    }
}
