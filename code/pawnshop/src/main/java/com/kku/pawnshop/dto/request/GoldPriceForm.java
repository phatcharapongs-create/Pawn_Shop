package com.kku.pawnshop.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** ฟอร์มบันทึกราคาทองรายวัน (หน้า /gold-prices) */
@Getter
@Setter
@NoArgsConstructor
public class GoldPriceForm {

    @NotNull(message = "กรุณาระบุวันที่")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate priceDate;

    @NotNull(message = "กรุณาระบุราคาทอง")
    @DecimalMin(value = "0.01", message = "ราคาทองต้องมากกว่า 0")
    private BigDecimal pricePerGram;

    /** ไม่บังคับ ใช้บันทึกว่าใครเป็นคนกรอกราคา */
    private Long employeeId;

    public static GoldPriceForm forToday() {
        GoldPriceForm form = new GoldPriceForm();
        form.setPriceDate(LocalDate.now());
        return form;
    }
}
