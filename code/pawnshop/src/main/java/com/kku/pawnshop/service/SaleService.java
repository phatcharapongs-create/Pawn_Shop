package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.SaleRecord;
import com.kku.pawnshop.domain.vo.Money;

import java.time.LocalDate;

/** เจ้าของ: สมาชิก E */
public interface SaleService {

    /** บันทึกการขายทรัพย์หลุด พร้อมเปลี่ยนสถานะตั๋วเป็น SOLD */
    SaleRecord recordSale(Long ticketId, Money soldPrice, String buyerName,
                          LocalDate soldDate, Long employeeId);
}
