package com.kku.pawnshop.controller.api;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.dto.request.OpenTicketRequest;
import com.kku.pawnshop.dto.request.SeizeTicketRequest;
import com.kku.pawnshop.dto.request.TicketTransactionRequest;
import com.kku.pawnshop.dto.response.TicketQuoteResponse;
import com.kku.pawnshop.dto.response.TicketResponse;
import com.kku.pawnshop.mapper.TicketMapper;
import com.kku.pawnshop.service.TicketAdminService;
import com.kku.pawnshop.service.TicketOperationService;
import com.kku.pawnshop.service.TicketQueryService;

import jakarta.validation.Valid;

/**
 * REST API ของตั๋วจำนำ
 *
 * ธุรกรรม (ต่อดอก / ไถ่ถอน / อายัด) ไม่ใช่ CRUD จึงออกแบบเป็น sub-resource ที่ POST สร้างขึ้น
 * เช่น POST /tickets/{id}/renewals แทน POST /renewTicket ตามหลัก resource-based naming
 *
 * Controller เรียกแค่ Service ไม่แตะ Repository และคืน DTO เสมอ
 */
@RestController
@RequestMapping("/api/v1")
public class TicketRestController {

    private final TicketQueryService queryService;
    private final TicketOperationService operationService;
    private final TicketAdminService adminService;
    private final TicketMapper mapper;

    public TicketRestController(TicketQueryService queryService, TicketOperationService operationService,
            TicketAdminService adminService, TicketMapper mapper) {
        this.queryService = queryService;
        this.operationService = operationService;
        this.adminService = adminService;
        this.mapper = mapper;
    }

    /** รายการตั๋ว รองรับ ?page=0&size=20&sort=pawnDate,desc */
    @GetMapping("/tickets")
    public Page<TicketResponse> list(
            @PageableDefault(size = 20, sort = "pawnDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return queryService.findAll(pageable).map(mapper::toResponse);
    }

    @GetMapping("/tickets/{id}")
    public TicketResponse findById(@PathVariable Long id) {
        return mapper.toResponse(queryService.findById(id));
    }

    @GetMapping("/tickets/number/{ticketNumber}")
    public TicketResponse findByNumber(@PathVariable String ticketNumber) {
        return mapper.toResponse(queryService.findByTicketNumber(ticketNumber));
    }

    @GetMapping("/customers/{customerId}/tickets")
    public Page<TicketResponse> findByCustomer(@PathVariable Long customerId,
            @PageableDefault(size = 20, sort = "pawnDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return queryService.findByCustomer(customerId, pageable).map(mapper::toResponse);
    }

    /** ยอดที่ต้องจ่าย ณ วันที่ (ไม่ส่ง asOf = วันนี้) */
    @GetMapping("/tickets/{id}/quote")
    public TicketQuoteResponse quote(@PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
        LocalDate date = asOf != null ? asOf : LocalDate.now();
        return new TicketQuoteResponse(id, date,
                queryService.quoteRenewalInterest(id, date).getAmount(),
                queryService.quoteRedemptionAmount(id, date).getAmount());
    }

    @PostMapping("/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse open(@Valid @RequestBody OpenTicketRequest request) {
        return mapper.toResponse(operationService.openTicket(
                request.getCustomerId(), request.getPledgedItemId(), Money.of(request.getPrincipal()), request.getEmployeeId()));
    }

    @PostMapping("/tickets/{id}/renewals")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse renew(@PathVariable Long id, @RequestBody(required = false) TicketTransactionRequest request) {
        TicketTransactionRequest r = request != null ? request : new TicketTransactionRequest(null, null);
        return mapper.toResponse(operationService.renewInterest(id, r.paymentDateOrToday(), r.employeeId()));
    }

    @PostMapping("/tickets/{id}/redemption")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse redeem(@PathVariable Long id, @RequestBody(required = false) TicketTransactionRequest request) {
        TicketTransactionRequest r = request != null ? request : new TicketTransactionRequest(null, null);
        return mapper.toResponse(operationService.redeem(id, r.paymentDateOrToday(), r.employeeId()));
    }

    @PostMapping("/tickets/{id}/seizure")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse seize(@PathVariable Long id, @Valid @RequestBody SeizeTicketRequest request) {
        return mapper.toResponse(adminService.seize(id, request.reason(), request.employeeId()));
    }

    @PostMapping("/tickets/{id}/seizure-release")
    public TicketResponse releaseSeizure(@PathVariable Long id,
            @RequestParam(required = false) Long employeeId) {
        return mapper.toResponse(adminService.releaseSeizure(id, employeeId));
    }
}
