package com.kku.pawnshop.controller.web;

import com.kku.pawnshop.dto.response.DailyReportResponse;
import com.kku.pawnshop.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor // เพิ่ม Constructor Injection ตามกฎข้อ 3
public class ReportWebController {

    private final ReportService reportService;

    @GetMapping("/daily")
    public String viewDailyReport(@RequestParam(required = false) LocalDate date, Model model) {
        
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        
        // 1. ดึงข้อมูลจาก Service ที่คำนวณและห่อ DTO มาให้เสร็จสรรพ
        DailyReportResponse report = reportService.getDailyReport(targetDate);
        
        // 2. ส่ง DTO ยัดใส่ Model คืนให้หน้าเว็บ (ถูกต้องตามกฎข้อ 2)
        model.addAttribute("reportDate", targetDate);
        model.addAttribute("summary", report.getSummary());
        model.addAttribute("entries", report.getEntries());
        
        return "reports/daily";
    }
}