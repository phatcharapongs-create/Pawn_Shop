package com.kku.pawnshop.dto.pledged;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppraisalResponse(Long id, Long pledgedItemId, BigDecimal appraisedValue,
        BigDecimal maxLoanAmount, BigDecimal goldPriceSnapshot, LocalDateTime appraisedAt, String note) { }
