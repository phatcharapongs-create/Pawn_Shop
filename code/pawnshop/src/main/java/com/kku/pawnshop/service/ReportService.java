package com.kku.pawnshop.service;

import com.kku.pawnshop.dto.response.DailyReportResponse;
import java.time.LocalDate;

public interface ReportService {
    DailyReportResponse getDailyReport(LocalDate date);
}