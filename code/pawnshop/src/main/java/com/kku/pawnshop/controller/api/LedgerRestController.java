package com.kku.pawnshop.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kku.pawnshop.domain.entity.LedgerEntry;
import com.kku.pawnshop.dto.response.LedgerEntryResponse;
import com.kku.pawnshop.mapper.LedgerEntryMapper;
import com.kku.pawnshop.service.LedgerService;

@RestController
@RequestMapping("/api/ledgers")
public class LedgerRestController {

    private final LedgerService ledgerService;
    private final LedgerEntryMapper ledgerEntryMapper;

    // =========================================================
    // สร้าง Constructor เองแทนการใช้ Lombok @RequiredArgsConstructor
    // =========================================================
    public LedgerRestController(LedgerService ledgerService, LedgerEntryMapper ledgerEntryMapper) {
        this.ledgerService = ledgerService;
        this.ledgerEntryMapper = ledgerEntryMapper;
    }

    @PostMapping("/{id}/reverse")
    public ResponseEntity<LedgerEntryResponse> reverseEntry(
            @PathVariable Long id,
            @RequestParam String reason) {
        
        // แก้ไข: ส่ง null แทน new Employee() ป้องกัน Error บันทึกพนักงานที่ไม่มีใน DB
        LedgerEntry reversedEntity = ledgerService.reverseEntry(id, null, reason);
        LedgerEntryResponse response = ledgerEntryMapper.toResponse(reversedEntity);
        
        return ResponseEntity.ok(response);
    }
}