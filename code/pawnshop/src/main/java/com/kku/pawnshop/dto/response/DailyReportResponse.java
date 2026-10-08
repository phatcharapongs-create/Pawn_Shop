package com.kku.pawnshop.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// ใช้ record เพื่อแพ็กข้อมูลรวมของวันนั้นๆ ส่งไปให้หน้าเว็บ HTML
public record DailyReportResponse(
        LocalDate reportDate,
        BigDecimal totalPrincipalReceived,
        BigDecimal totalInterestReceived,
        BigDecimal grandTotal,
        int totalTransactions,
        List<LedgerEntryResponse> entries
) {}