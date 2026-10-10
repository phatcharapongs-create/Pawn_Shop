package com.kku.pawnshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Public self-registration is limited to the CUSTOMER role. */
@Getter
@Setter
@NoArgsConstructor
public class CustomerSignupForm extends CustomerRequest {
    @NotBlank(message = "กรุณาตั้งชื่อผู้ใช้")
    @Size(min = 3, max = 80, message = "ชื่อผู้ใช้ต้องมีความยาว 3–80 ตัวอักษร")
    private String username;

    @NotBlank(message = "กรุณาตั้งรหัสผ่าน")
    @Size(min = 12, message = "รหัสผ่านต้องมีอย่างน้อย 12 ตัวอักษร")
    private String password;

    @NotBlank(message = "กรุณายืนยันรหัสผ่าน")
    private String confirmPassword;
}
