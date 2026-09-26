package com.kku.pawnshop.event;

import java.time.LocalDate;

/** ตั๋วใกล้ครบกำหนดไถ่ถอน ปล่อยโดย scheduled job ประจำวัน */
public record TicketDueSoonEvent(Long ticketId, String ticketNumber, Long customerId, LocalDate dueDate) {
}
