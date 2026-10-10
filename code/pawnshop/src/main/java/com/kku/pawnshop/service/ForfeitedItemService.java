package com.kku.pawnshop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kku.pawnshop.domain.enums.TicketStatus;
import com.kku.pawnshop.dto.response.ForfeitedItemResponse;
import com.kku.pawnshop.dto.response.ForfeitedSummary;
import com.kku.pawnshop.dto.response.CustomerProductResponse;

/** ISP: ฝั่งอ่านอย่างเดียวของทรัพย์หลุดจำนำ (การขายอยู่ที่ SaleService) */
public interface ForfeitedItemService {

    /** status = FORFEITED หรือ SOLD ถ้าส่ง null จะแสดงทั้งสองสถานะ */
    Page<ForfeitedItemResponse> list(TicketStatus status, Pageable pageable);

    ForfeitedSummary summary();

    /** รายการทรัพย์พร้อมจำหน่ายสำหรับแสดงในบัญชีลูกค้า โดยไม่ส่งข้อมูลเจ้าของเดิม */
    Page<CustomerProductResponse> listAvailableProducts(Pageable pageable);
}
