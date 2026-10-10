package com.kku.pawnshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotBlank(message = "กรุณากรอกชื่อ-นามสกุล")
    private String name;

    @NotBlank(message = "กรุณากรอกเลขประจำตัวประชาชน")
    @Pattern(regexp = "^\\d{13}$", message = "เลขประจำตัวประชาชนต้องเป็นตัวเลข 13 หลักเท่านั้น")
    private String citizenId;

    @NotBlank(message = "กรุณากรอกเบอร์โทรศัพท์")
    private String phoneNumber;

    private String address;
}
