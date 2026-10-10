package com.kku.pawnshop.controller.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kku.pawnshop.domain.enums.TicketStatus;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.service.ForfeitedItemService;
import com.kku.pawnshop.service.SaleService;
import com.kku.pawnshop.service.TicketAdminService;

import lombok.RequiredArgsConstructor;

/** หน้าทรัพย์หลุดจำนำ — URL /forfeited-items ตรงกับเมนูใน layout.html */
@Controller
@RequestMapping("/forfeited-items")
@RequiredArgsConstructor
public class ForfeitedItemWebController {

    private static final int PAGE_SIZE = 20;

    private final ForfeitedItemService itemService;
    private final SaleService saleService;
    private final TicketAdminService ticketAdminService;

    @GetMapping
    public String list(@RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page, Model model) {
        TicketStatus filter = parseFilter(status);

        model.addAttribute("statusFilter", filter != null ? filter.name() : null);
        model.addAttribute("summary", itemService.summary());
        model.addAttribute("items", itemService.list(filter,
                PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by(Sort.Direction.DESC, "graceEndDate"))));
        return "forfeited-items/list";
    }

    @PostMapping("/process-status")
    public String processStatus(RedirectAttributes redirect) {
        LocalDate today = LocalDate.now();
        int movedToGrace = ticketAdminService.promoteOverdueToGrace(today).size();
        int newlyForfeited = ticketAdminService.forfeitExpired(today).size();
        redirect.addFlashAttribute("success", "ตรวจสอบสถานะแล้ว: เข้าช่วงผ่อนผัน " + movedToGrace
                + " รายการ, หลุดจำนำ " + newlyForfeited + " รายการ");
        return "redirect:/forfeited-items";
    }

    @PostMapping("/{ticketId}/sell")
    public String sell(@PathVariable Long ticketId,
            @RequestParam BigDecimal soldPrice,
            @RequestParam(required = false) String buyerName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate soldDate,
            @RequestParam(required = false) Long employeeId,
            RedirectAttributes redirect) {
        try {
            saleService.recordSale(ticketId, Money.of(soldPrice), buyerName, soldDate, employeeId);
            redirect.addFlashAttribute("success", "บันทึกการขายเรียบร้อย");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/forfeited-items";
    }

    /** รับเฉพาะ FORFEITED / SOLD ค่าอื่นหรือค่าว่างถือว่าไม่กรอง */
    private TicketStatus parseFilter(String status) {
        if ("FORFEITED".equals(status)) {
            return TicketStatus.FORFEITED;
        }
        if ("SOLD".equals(status)) {
            return TicketStatus.SOLD;
        }
        return null;
    }
}
