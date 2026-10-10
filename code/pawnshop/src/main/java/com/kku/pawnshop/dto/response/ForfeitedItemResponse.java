package com.kku.pawnshop.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/** ทรัพย์หลุดจำนำหนึ่งรายการ (status = FORFEITED รอจำหน่าย หรือ SOLD จำหน่ายแล้ว) */
public record ForfeitedItemResponse(
        Long ticketId,
        String ticketNumber,
        String customerName,
        BigDecimal principal,
        LocalDate graceEndDate,
        String status,
        String itemType,
        String itemDescription,
        String storageSlot,
        BigDecimal soldPrice,
        LocalDate soldDate,
        String buyerName) {
}
