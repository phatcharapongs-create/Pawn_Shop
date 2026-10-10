package com.kku.pawnshop.dto.pledged;

import com.kku.pawnshop.domain.enums.ItemType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppraisalHistoryRow(Long id, Long pledgedItemId, ItemType itemType, String description,
        String serialNumber, BigDecimal appraisedValue, BigDecimal maxLoanAmount, LocalDateTime appraisedAt) { }
