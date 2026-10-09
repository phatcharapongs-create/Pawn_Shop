package com.kku.pawnshop.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DailyReportResponse {
    private SummaryDto summary;
    private List<EntryDto> entries;

    @Data
    @Builder
    public static class SummaryDto {
        private MoneyDto totalPawned;
        private MoneyDto totalInterest;
        private MoneyDto totalRedeemed;
        private int totalEntriesCount;
    }

    @Data
    @Builder
    public static class MoneyDto {
        private BigDecimal amount;
    }

    @Data
    @Builder
    public static class EntryDto {
        private String time;
        private String ticketNumber;
        private String entryType;
        private MoneyDto principalAmount;
        private MoneyDto interestAmount;
        private MoneyDto totalAmount;
        private String handledBy;
    }
}