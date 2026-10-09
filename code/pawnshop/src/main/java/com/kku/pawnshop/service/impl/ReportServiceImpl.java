package com.kku.pawnshop.service.impl;

import com.kku.pawnshop.domain.entity.LedgerEntry;
import com.kku.pawnshop.dto.response.DailyReportResponse;
import com.kku.pawnshop.repository.LedgerEntryRepository;
import com.kku.pawnshop.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final LedgerEntryRepository ledgerEntryRepository;

    @Override
    @Transactional(readOnly = true)
    public DailyReportResponse getDailyReport(LocalDate date) {
        // 1. ดึงข้อมูลจากฐานข้อมูล
        List<LedgerEntry> entries = ledgerEntryRepository.findByEntryDate(date);

        BigDecimal totalPawned = BigDecimal.ZERO;
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalRedeemed = BigDecimal.ZERO;

        // 2. แปลง Entity ให้เป็น DTO สำหรับแสดงผลในตาราง
        List<DailyReportResponse.EntryDto> entryDtos = entries.stream().map(entry -> {
            BigDecimal principal = entry.getPrincipalAmount() != null ? entry.getPrincipalAmount().getAmount() : BigDecimal.ZERO;
            BigDecimal interest = entry.getInterestAmount() != null ? entry.getInterestAmount().getAmount() : BigDecimal.ZERO;
            BigDecimal total = entry.getTotalAmount() != null ? entry.getTotalAmount().getAmount() : BigDecimal.ZERO;

            return DailyReportResponse.EntryDto.builder()
                    .time(entry.getCreatedAt() != null ? entry.getCreatedAt().toLocalTime().toString() : "")
                    .ticketNumber(entry.getTicket() != null ? String.valueOf(entry.getTicket().getId()) : "-")
                    .entryType(entry.getEntryType() != null ? entry.getEntryType().name() : "")
                    .principalAmount(DailyReportResponse.MoneyDto.builder().amount(principal).build())
                    .interestAmount(DailyReportResponse.MoneyDto.builder().amount(interest).build())
                    .totalAmount(DailyReportResponse.MoneyDto.builder().amount(total).build())
                    .handledBy(entry.getHandledBy() != null ? String.valueOf(entry.getHandledBy().getId()) : "-")
                    .build();
        }).collect(Collectors.toList());

        // 3. คำนวณสรุปยอดตามประเภทธุรกรรม
        for (LedgerEntry entry : entries) {
            BigDecimal principal = entry.getPrincipalAmount() != null ? entry.getPrincipalAmount().getAmount() : BigDecimal.ZERO;
            BigDecimal interest = entry.getInterestAmount() != null ? entry.getInterestAmount().getAmount() : BigDecimal.ZERO;
            String type = entry.getEntryType() != null ? entry.getEntryType().name() : "";

            if ("PAWN".equals(type)) { // เงินต้นรับจำนำ
                totalPawned = totalPawned.add(principal);
            } else if ("REDEMPTION".equals(type)) { // ไถ่ถอน
                totalRedeemed = totalRedeemed.add(principal);
                totalInterest = totalInterest.add(interest);
            } else { // อื่นๆ เช่น ต่อดอก (RENEWAL)
                totalInterest = totalInterest.add(interest);
            }
        }

        DailyReportResponse.SummaryDto summary = DailyReportResponse.SummaryDto.builder()
                .totalPawned(DailyReportResponse.MoneyDto.builder().amount(totalPawned).build())
                .totalInterest(DailyReportResponse.MoneyDto.builder().amount(totalInterest).build())
                .totalRedeemed(DailyReportResponse.MoneyDto.builder().amount(totalRedeemed).build())
                .totalEntriesCount(entries.size())
                .build();

        return DailyReportResponse.builder()
                .summary(summary)
                .entries(entryDtos)
                .build();
    }
}