package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.enums.TicketStatus;
import com.kku.pawnshop.dto.response.AdminDashboardView;
import com.kku.pawnshop.repository.CustomerRepository;
import com.kku.pawnshop.repository.EmployeeRepository;
import com.kku.pawnshop.repository.GoldPriceRepository;
import com.kku.pawnshop.repository.PawnTicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminDashboardService {
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final PawnTicketRepository ticketRepository;
    private final GoldPriceRepository goldPriceRepository;

    public AdminDashboardService(CustomerRepository customerRepository, EmployeeRepository employeeRepository,
                                 PawnTicketRepository ticketRepository, GoldPriceRepository goldPriceRepository) {
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.ticketRepository = ticketRepository;
        this.goldPriceRepository = goldPriceRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardView dashboard() {
        return new AdminDashboardView(
                customerRepository.count(), employeeRepository.count(),
                ticketRepository.countByStatus(TicketStatus.ACTIVE),
                ticketRepository.countByStatus(TicketStatus.GRACE),
                ticketRepository.countByStatus(TicketStatus.FORFEITED),
                ticketRepository.countByStatus(TicketStatus.SOLD),
                ticketRepository.countByStatus(TicketStatus.REDEEMED),
                ticketRepository.countByStatus(TicketStatus.SEIZED),
                goldPriceRepository.findTopByPriceDateLessThanEqualOrderByPriceDateDesc(java.time.LocalDate.now())
                        .map(price -> price.getPriceDate()).orElse(null));
    }
}
