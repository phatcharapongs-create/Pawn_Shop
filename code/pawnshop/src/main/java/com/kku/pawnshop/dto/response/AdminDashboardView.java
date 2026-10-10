package com.kku.pawnshop.dto.response;

import java.time.LocalDate;

public record AdminDashboardView(
        long customerCount,
        long employeeCount,
        long activeTickets,
        long graceTickets,
        long forfeitedTickets,
        long soldTickets,
        long redeemedTickets,
        long seizedTickets,
        LocalDate latestGoldPriceDate) {}
