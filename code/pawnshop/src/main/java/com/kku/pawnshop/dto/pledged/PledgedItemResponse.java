package com.kku.pawnshop.dto.pledged;

import com.kku.pawnshop.domain.enums.ItemType;
import java.math.BigDecimal;

public record PledgedItemResponse(Long id, ItemType itemType, String description, String serialNumber,
        BigDecimal weightGram, BigDecimal purityPercent, Integer manufactureYear, BigDecimal referencePrice,
        Integer conditionGrade, String storageSlot, String photoUrl) { }
