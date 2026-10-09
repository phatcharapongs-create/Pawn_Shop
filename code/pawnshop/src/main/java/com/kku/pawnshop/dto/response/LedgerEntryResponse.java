package com.kku.pawnshop.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LedgerEntryResponse(
        Long id,
        Long ticketId,
        String entryType,
        BigDecimal principalAmount,
        BigDecimal interestAmount,
        BigDecimal totalAmount,
        LocalDate entryDate,
        String note,
        LocalDateTime createdAt
) {}