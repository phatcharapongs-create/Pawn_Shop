package com.kku.pawnshop.controller.web;

import com.kku.pawnshop.domain.entity.Appraisal;
import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.enums.ItemType;
import com.kku.pawnshop.dto.pledged.PledgedItemRequest;
import com.kku.pawnshop.dto.pledged.AppraisalDetailView;
import com.kku.pawnshop.mapper.PledgedItemMapper;
import com.kku.pawnshop.service.AppraisalService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/appraisals")
public class AppraisalWebController {
    private final AppraisalService appraisalService;
    private final PledgedItemMapper mapper;
    public AppraisalWebController(AppraisalService appraisalService, PledgedItemMapper mapper) {
        this.appraisalService = appraisalService;
        this.mapper = mapper;
    }
    @GetMapping("/new")
    public String newAppraisal(Model model) {
        model.addAttribute("item", new PledgedItemRequest());
        model.addAttribute("itemTypes", ItemType.values());
        return "appraisals/new";
    }

    @GetMapping
    public String history(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), 20);
        model.addAttribute("appraisals", appraisalService.findHistory(pageable));
        return "appraisals/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        AppraisalDetailView detail = appraisalService.findDetail(id);
        model.addAttribute("item", detail.item());
        model.addAttribute("appraisal", detail.appraisal());
        return "appraisals/result";
    }
    @PostMapping
    public String create(@Valid @ModelAttribute("item") PledgedItemRequest request, BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("itemTypes", ItemType.values());
            return "appraisals/new";
        }
        PledgedItem item = mapper.toEntity(request);
        Appraisal appraisal = appraisalService.appraise(item, request.getAppraiserId());
        model.addAttribute("item", mapper.toResponse(appraisal.getPledgedItem()));
        model.addAttribute("appraisal", mapper.toResponse(appraisal));
        return "appraisals/result";
    }
}
