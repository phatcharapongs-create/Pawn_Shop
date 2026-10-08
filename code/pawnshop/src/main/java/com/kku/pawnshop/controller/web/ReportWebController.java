package com.kku.pawnshop.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/reports")
public class ReportWebController {

    // (ในอนาคตถ้ามี ReportService สำหรับคำนวณยอดรวม ค่อยมาทำ Constructor Injection ตรงนี้ครับ)
    public ReportWebController() {
    }

    @GetMapping("/daily")
    public String viewDailyReport(@RequestParam(required = false) LocalDate date, Model model) {
        
        // ถ้าไม่ได้ระบุวันที่มา ให้ใช้วันที่ปัจจุบัน
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        
        // TODO: ดึงข้อมูล DailyReportResponse มาใส่ในโมเดลเพื่อส่งไปหน้าเว็บ
        // DailyReportResponse report = reportService.getDailyReport(targetDate);
        // model.addAttribute("report", report);
        
        model.addAttribute("reportDate", targetDate);
        
        // ส่งไปแสดงผลที่ไฟล์ src/main/resources/templates/reports/daily.html
        return "reports/daily";
    }
}