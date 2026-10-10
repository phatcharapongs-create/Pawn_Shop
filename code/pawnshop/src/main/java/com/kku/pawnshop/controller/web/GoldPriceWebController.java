package com.kku.pawnshop.controller.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.dto.request.GoldPriceForm;
import com.kku.pawnshop.service.GoldPriceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** หน้าราคาทองรายวัน — URL /gold-prices ตรงกับเมนูใน layout.html */
@Controller
@RequestMapping("/gold-prices")
@RequiredArgsConstructor
public class GoldPriceWebController {

    private static final int PAGE_SIZE = 20;

    private final GoldPriceService goldPriceService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        populate(model, page);
        model.addAttribute("priceForm", GoldPriceForm.forToday());
        return "gold-prices/list";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("priceForm") GoldPriceForm form, BindingResult errors,
            Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            populate(model, 0);
            return "gold-prices/list";
        }
        try {
            goldPriceService.recordPrice(form.getPriceDate(), Money.of(form.getPricePerGram()), form.getEmployeeId());
            redirect.addFlashAttribute("success", "บันทึกราคาทองเรียบร้อย");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gold-prices";
    }

    private void populate(Model model, int page) {
        model.addAttribute("latest", goldPriceService.findLatest().orElse(null));
        model.addAttribute("prices", goldPriceService.findAll(
                PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by(Sort.Direction.DESC, "priceDate"))));
    }
}
