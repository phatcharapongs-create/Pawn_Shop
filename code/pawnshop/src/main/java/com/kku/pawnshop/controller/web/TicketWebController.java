package com.kku.pawnshop.controller.web;

import java.time.LocalDate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.dto.request.OpenTicketRequest;
import com.kku.pawnshop.mapper.TicketMapper;
import com.kku.pawnshop.service.TicketOperationService;
import com.kku.pawnshop.service.TicketQueryService;

import jakarta.validation.Valid;

/**
 * หน้าเว็บตั๋วจำนำ (Thymeleaf) — ใช้ Service ชุดเดียวกับ REST API ไม่มี logic ซ้ำ
 * URL /pawn-tickets ให้ตรงกับเมนูใน layout.html ของพิชญพงษ์
 */
@Controller
@RequestMapping("/pawn-tickets")
public class TicketWebController {

    private final TicketQueryService queryService;
    private final TicketOperationService operationService;
    private final TicketMapper mapper;

    public TicketWebController(TicketQueryService queryService, TicketOperationService operationService,
            TicketMapper mapper) {
        this.queryService = queryService;
        this.operationService = operationService;
        this.mapper = mapper;
    }

    /** รายการตั๋ว + ค้นหาด้วยเลขที่ */
    @GetMapping
    public String list(@RequestParam(required = false) String number,
            @RequestParam(defaultValue = "0") int page, Model model) {
        if (number != null && !number.isBlank()) {
            try {
                PawnTicket found = queryService.findByTicketNumber(number.trim());
                return "redirect:/pawn-tickets/" + found.getId();
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        var tickets = queryService.findAll(PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "pawnDate")))
                .map(mapper::toResponse);
        model.addAttribute("tickets", tickets);
        model.addAttribute("number", number);
        return "pawn-tickets/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new OpenTicketRequest());
        return "pawn-tickets/new";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") OpenTicketRequest form, BindingResult errors,
            Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return "pawn-tickets/new";
        }
        try {
            PawnTicket ticket = operationService.openTicket(
                    form.getCustomerId(), form.getPledgedItemId(), Money.of(form.getPrincipal()), form.getEmployeeId());
            redirect.addFlashAttribute("success", "ออกตั๋วเลขที่ " + ticket.getTicketNumber() + " เรียบร้อย");
            return "redirect:/pawn-tickets/" + ticket.getId();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "pawn-tickets/new";
        }
    }

    /** รายละเอียดตั๋ว + ยอดที่ต้องจ่ายวันนี้ + ปุ่มต่อดอก/ไถ่ถอน */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var ticket = mapper.toResponse(queryService.findById(id));
        model.addAttribute("ticket", ticket);
        if (ticket.canRenew() || ticket.canRedeem()) {
            LocalDate today = LocalDate.now();
            model.addAttribute("renewalInterest", queryService.quoteRenewalInterest(id, today).getAmount());
            model.addAttribute("redemptionAmount", queryService.quoteRedemptionAmount(id, today).getAmount());
        }
        return "pawn-tickets/detail";
    }

    @PostMapping("/{id}/renew")
    public String renew(@PathVariable Long id, @RequestParam(required = false) Long employeeId,
            RedirectAttributes redirect) {
        try {
            operationService.renewInterest(id, LocalDate.now(), employeeId);
            redirect.addFlashAttribute("success", "ต่อดอกเรียบร้อย");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pawn-tickets/" + id;
    }

    @PostMapping("/{id}/redeem")
    public String redeem(@PathVariable Long id, @RequestParam(required = false) Long employeeId,
            RedirectAttributes redirect) {
        try {
            operationService.redeem(id, LocalDate.now(), employeeId);
            redirect.addFlashAttribute("success", "ไถ่ถอนเรียบร้อย");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pawn-tickets/" + id;
    }
}
