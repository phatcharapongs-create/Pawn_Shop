package com.kku.pawnshop.domain.entity;

import com.kku.pawnshop.domain.enums.ItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * ทรัพย์ที่นำมาจำนำ — เจ้าของ: สมาชิก B
 *
 * หมายเหตุการออกแบบ (LSP): ไม่ทำ GoldItem / PhoneItem เป็น subclass
 * เพราะ PhoneItem จะตอบ getWeightGram() ไม่ได้ ต้องโยน exception = ละเมิด LSP
 * จึงใช้คลาสเดียวที่มีฟิลด์เป็น nullable แล้วให้ AppraisalStrategy
 * แต่ละตัวเป็นผู้รู้ว่าฟิลด์ไหนจำเป็นสำหรับประเภทนั้น
 */
@Entity
@Table(name = "pledged_item", indexes = {
        @Index(name = "idx_item_serial", columnList = "serial_number"),
        @Index(name = "idx_item_type", columnList = "item_type")
})
@Getter
@Setter
@NoArgsConstructor
public class PledgedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private ItemType itemType;

    @Column(name = "description", nullable = false, length = 300)
    private String description;

    /** สำหรับเครื่องใช้ไฟฟ้าและนาฬิกา ใช้ตรวจสอบกับบัญชีทรัพย์อายัด */
    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    /** สำหรับทองคำเท่านั้น หน่วยเป็นกรัม */
    @Column(name = "weight_gram", precision = 10, scale = 3)
    private BigDecimal weightGram;

    /** สำหรับทองคำ เช่น 96.5 หมายถึงทอง 96.5% */
    @Column(name = "purity_percent", precision = 5, scale = 2)
    private BigDecimal purityPercent;

    /** สำหรับเครื่องใช้ไฟฟ้า ใช้คิดค่าเสื่อม */
    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    /** คะแนนสภาพ 1-5 ใช้เป็นตัวคูณลดราคา */
    @Column(name = "condition_grade")
    private Integer conditionGrade;

    @Column(name = "storage_slot", length = 30)
    private String storageSlot;

    @Column(name = "photo_url", length = 300)
    private String photoUrl;
}
