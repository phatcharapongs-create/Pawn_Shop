package com.kku.pawnshop.dto.response;

import java.math.BigDecimal;

/** ข้อมูลสินค้าสาธารณะที่ลูกค้าดูได้ ไม่รวมเลขตั๋ว เงินต้น หรือลูกค้าเจ้าของเดิม */
public record CustomerProductResponse(
        String itemType,
        String description,
        String photoUrl,
        Integer conditionGrade,
        BigDecimal weightGram,
        BigDecimal purityPercent,
        Integer manufactureYear) {
}
