package com.kku.pawnshop.mapper;

import org.springframework.stereotype.Component;

import com.kku.pawnshop.domain.entity.LedgerEntry;
import com.kku.pawnshop.dto.response.LedgerEntryResponse;

@Component
public class LedgerEntryMapper {
    
    public LedgerEntryResponse toResponse(LedgerEntry entity) {
        return new LedgerEntryResponse(
                entity.getId(),
                entity.getTicket().getId(),
                entity.getEntryType().name(),
                entity.getPrincipalAmount().getAmount(),
                entity.getInterestAmount().getAmount(),
                entity.getTotalAmount().getAmount(),
                entity.getEntryDate(),
                entity.getNote(),
                entity.getCreatedAt()
        );
    }
}