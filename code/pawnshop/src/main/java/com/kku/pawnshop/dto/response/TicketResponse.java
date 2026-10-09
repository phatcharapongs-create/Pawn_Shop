package com.kku.pawnshop.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/** ข้อมูลตั๋วที่ส่งออกไปทาง API และหน้าเว็บ — ไม่ส่ง entity ออกไปตรง ๆ */
public record TicketResponse(
        Long id,
        String ticketNumber,
        Long customerId,
        String customerName,
        Long pledgedItemId,
        String itemDescription,
        BigDecimal principal,
        LocalDate pawnDate,
        LocalDate dueDate,
        LocalDate graceEndDate,
        LocalDate interestPaidUntil,
        String status,
        String statusDisplay,
        boolean canRenew,
        boolean canRedeem) {
}
