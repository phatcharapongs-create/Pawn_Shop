package com.kku.pawnshop.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/** ยอดที่ต้องจ่าย ณ วันที่ระบุ (ยังไม่ใช่การจ่ายจริง) */
public record TicketQuoteResponse(
        Long ticketId,
        LocalDate asOf,
        BigDecimal renewalInterest,
        BigDecimal redemptionAmount) {
}
