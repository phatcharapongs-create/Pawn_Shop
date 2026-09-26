package com.kku.pawnshop.service.appraisal;

import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.enums.ItemType;
import com.kku.pawnshop.domain.vo.Money;

import java.math.BigDecimal;

/**
 * Strategy Pattern — วิธีตีราคาทรัพย์แต่ละประเภท
 *
 * ทองคิดจาก น้ำหนัก x ราคาทองวันนี้ x ความบริสุทธิ์
 * เครื่องใช้ไฟฟ้าคิดจาก ราคาตั้งต้น ลบค่าเสื่อมตามอายุ
 * นาฬิกาและเครื่องประดับคิดจากราคาอ้างอิงรุ่น
 *
 * OCP: เพิ่มประเภททรัพย์ใหม่ = เพิ่มคลาสที่ implement อินเทอร์เฟซนี้
 * ไม่ต้องแก้โค้ดเดิมแม้แต่บรรทัดเดียว เพราะ resolver ใช้ supports() เลือกเอง
 */
public interface AppraisalStrategy {

    /** รองรับทรัพย์ประเภทนี้หรือไม่ */
    boolean supports(ItemType type);

    /** มูลค่าตลาดที่ประเมินได้ */
    Money appraise(PledgedItem item);

    /**
     * อัตราส่วนวงเงินจำนำต่อมูลค่าประเมิน (Loan To Value)
     * ทองให้สูงเพราะสภาพคล่องดี เครื่องใช้ไฟฟ้าให้ต่ำเพราะเสื่อมเร็ว
     */
    BigDecimal loanToValueRatio();

    /** วงเงินสูงสุดที่ให้จำนำได้ */
    default Money maxLoanAmount(PledgedItem item) {
        return appraise(item).multiply(loanToValueRatio());
    }
}
