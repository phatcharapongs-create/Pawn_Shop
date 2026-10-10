package com.kku.pawnshop.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/** ราคาทองหนึ่งวัน สำหรับแสดงบนหน้าเว็บ — ไม่ส่ง entity ออกไปตรง ๆ */
public record GoldPriceResponse(
        LocalDate priceDate,
        BigDecimal pricePerGram,
        String recordedByName) {
}
