package com.kku.pawnshop.controller.api;

import com.kku.pawnshop.domain.entity.Appraisal;
import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.dto.pledged.*;
import com.kku.pawnshop.mapper.PledgedItemMapper;
import com.kku.pawnshop.service.AppraisalService;
import com.kku.pawnshop.service.PledgedItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pledged-items")
public class PledgedItemRestController {
    private final PledgedItemService pledgedItemService;
    private final AppraisalService appraisalService;
    private final PledgedItemMapper mapper;
    public PledgedItemRestController(PledgedItemService pledgedItemService, AppraisalService appraisalService, PledgedItemMapper mapper) {
        this.pledgedItemService = pledgedItemService; this.appraisalService = appraisalService; this.mapper = mapper;
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PledgedItemResponse register(@Valid @RequestBody PledgedItemRequest request) {
        return mapper.toResponse(pledgedItemService.register(mapper.toEntity(request)));
    }
    @GetMapping("/{id}")
    public PledgedItemResponse findById(@PathVariable Long id) { return mapper.toResponse(pledgedItemService.findById(id)); }
    @PostMapping("/{id}/appraisals") @ResponseStatus(HttpStatus.CREATED)
    public AppraisalResponse appraise(@PathVariable Long id, @RequestBody(required = false) Map<String, Long> body) {
        PledgedItem item = pledgedItemService.findById(id);
        Long appraiserId = body == null ? null : body.get("appraiserId");
        Appraisal appraisal = appraisalService.appraise(item, appraiserId);
        return mapper.toResponse(appraisal);
    }
    @GetMapping("/{id}/appraisals")
    public AppraisalResponse getAppraisal(@PathVariable Long id) { return mapper.toResponse(appraisalService.findByPledgedItemId(id)); }
}
