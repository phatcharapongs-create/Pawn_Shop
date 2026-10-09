package com.kku.pawnshop.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * คำขอรับจำนำ — ทรัพย์ต้องผ่านการประเมินมาก่อนแล้ว (pledgedItemId จากหน้าประเมินราคา)
 * เป็นคลาสมี getter/setter (ไม่ใช่ record) เพื่อให้ th:field ของ Thymeleaf ผูกฟอร์มได้
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenTicketRequest {

    @NotNull(message = "ต้องระบุลูกค้า")
    private Long customerId;

    @NotNull(message = "ต้องระบุทรัพย์ที่ประเมินแล้ว")
    private Long pledgedItemId;

    @NotNull(message = "ต้องระบุเงินต้น")
    @DecimalMin(value = "1.00", message = "เงินต้นต้องอย่างน้อย 1 บาท")
    private BigDecimal principal;

    private Long employeeId;
}
