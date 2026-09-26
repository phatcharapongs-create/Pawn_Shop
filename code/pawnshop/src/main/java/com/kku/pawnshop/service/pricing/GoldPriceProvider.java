package com.kku.pawnshop.service.pricing;

import com.kku.pawnshop.domain.vo.Money;

import java.time.LocalDate;

/**
 * แหล่งราคาทอง
 *
 * DIP: GoldAppraisalStrategy ขึ้นกับอินเทอร์เฟซนี้ ไม่ใช่ขึ้นกับตารางฐานข้อมูล
 * หรือ API ภายนอกโดยตรง ทำให้เขียน unit test ของสูตรประเมินได้
 * โดยใช้ Mockito จำลองราคาคงที่ ไม่ต้องต่อเน็ตและไม่ต้องมีข้อมูลในฐานข้อมูล
 *
 * เวอร์ชันที่ส่งใช้ DbGoldPriceProvider อ่านจากตาราง gold_price
 * ถ้าอนาคตจะต่อ API จริง แค่เขียน implementation ใหม่
 */
public interface GoldPriceProvider {

    /** ราคาทองต่อกรัม ณ วันที่กำหนด ถ้าไม่มีข้อมูลวันนั้นให้ใช้ราคาล่าสุดก่อนหน้า */
    Money pricePerGram(LocalDate date);
}
