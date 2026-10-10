package com.kku.pawnshop.controller.web;

import com.kku.pawnshop.service.AdminDashboardService;
import com.kku.pawnshop.service.TicketAdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class AdminWebController {
    private final AdminDashboardService dashboardService;
    private final TicketAdminService ticketAdminService;

    public AdminWebController(AdminDashboardService dashboardService, TicketAdminService ticketAdminService) {
        this.dashboardService = dashboardService;
        this.ticketAdminService = ticketAdminService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("dashboard", dashboardService.dashboard());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("isAdminDashboard", true);
        return "admin/dashboard";
    }

    @PostMapping("/admin/run-daily-jobs")
    public String runDailyJobs(RedirectAttributes redirect) {
        LocalDate today = LocalDate.now();
        int movedToGrace = ticketAdminService.promoteOverdueToGrace(today).size();
        int forfeited = ticketAdminService.forfeitExpired(today).size();
        redirect.addFlashAttribute("jobMessage",
                "อัปเดตสถานะแล้ว: เข้าช่วงผ่อนผัน " + movedToGrace + " รายการ, หลุดจำนำ " + forfeited + " รายการ");
        return "redirect:/admin";
    }
}
