package com.kku.pawnshop.domain.enums;

/** สถานะของตั๋วจำนำ ใช้เป็นกุญแจชี้ไปยัง TicketState ที่รับผิดชอบสถานะนั้น */
public enum TicketStatus {
    ACTIVE,
    GRACE,
    REDEEMED,
    FORFEITED,
    SOLD,
    SEIZED
}
