package com.kku.pawnshop.dto.request;

import java.time.LocalDate;

/** ใช้ทั้งต่อดอกและไถ่ถอน — ถ้าไม่ส่ง paymentDate จะใช้วันนี้ */
public record TicketTransactionRequest(LocalDate paymentDate, Long employeeId) {

    public LocalDate paymentDateOrToday() {
        return paymentDate != null ? paymentDate : LocalDate.now();
    }
}
