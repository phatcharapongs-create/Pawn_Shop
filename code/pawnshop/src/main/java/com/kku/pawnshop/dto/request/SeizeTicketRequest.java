package com.kku.pawnshop.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SeizeTicketRequest(
        @NotBlank(message = "ต้องระบุเหตุผลการอายัด") String reason,
        Long employeeId) {
}
