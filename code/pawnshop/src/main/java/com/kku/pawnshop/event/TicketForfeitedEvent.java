package com.kku.pawnshop.event;

/** ตั๋วหลุดจำนำ ทรัพย์ตกเป็นของร้าน */
public record TicketForfeitedEvent(Long ticketId, String ticketNumber, Long customerId, Long pledgedItemId) {
}
