package com.kku.pawnshop.domain.entity;

import com.kku.pawnshop.domain.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * บันทึกการจำหน่ายทรัพย์หลุดจำนำ — เจ้าของ: สมาชิก E
 *
 * ความสัมพันธ์ One-to-One กับ PledgedItem (ทรัพย์ 1 ชิ้นขายได้ครั้งเดียว)
 */
@Entity
@Table(name = "sale_record")
@Getter
@Setter
@NoArgsConstructor
public class SaleRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pledged_item_id", nullable = false, unique = true)
    private PledgedItem pledgedItem;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "sold_price", nullable = false, precision = 15, scale = 2))
    private Money soldPrice;

    @Column(name = "sold_date", nullable = false)
    private LocalDate soldDate;

    @Column(name = "buyer_name", length = 200)
    private String buyerName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by")
    private Employee handledBy;
}
