package com.kku.pawnshop.dto.response;

import java.math.BigDecimal;

/** ตัวเลขสรุปบนหน้าทรัพย์หลุดจำนำ */
public record ForfeitedSummary(
        long forfeitedCount,
        BigDecimal forfeitedPrincipal,
        long soldCount,
        BigDecimal soldTotal) {
}
