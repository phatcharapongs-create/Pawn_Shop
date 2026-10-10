package com.kku.pawnshop.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.dto.response.GoldPriceResponse;

/** ราคาทองรายวัน: ดูราคาล่าสุด ดูประวัติ และบันทึกราคาของแต่ละวัน */
public interface GoldPriceService {

    /** ราคาของวันที่ใหม่ที่สุดที่บันทึกไว้ ถ้ายังไม่มีข้อมูลเลยคืนค่าว่าง */
    Optional<GoldPriceResponse> findLatest();

    Page<GoldPriceResponse> findAll(Pageable pageable);

    /** บันทึกราคาของวันที่กำหนด ถ้าวันนั้นมีราคาอยู่แล้วจะแทนที่ (1 วันมีได้ 1 ราคา) */
    GoldPriceResponse recordPrice(LocalDate priceDate, Money pricePerGram, Long employeeId);
}
