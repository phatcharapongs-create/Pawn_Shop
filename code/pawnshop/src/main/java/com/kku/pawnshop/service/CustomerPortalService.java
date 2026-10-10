package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.UserAccount;
import com.kku.pawnshop.domain.enums.AccountRole;
import com.kku.pawnshop.dto.response.TicketResponse;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import com.kku.pawnshop.mapper.TicketMapper;
import com.kku.pawnshop.repository.UserAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerPortalService {
    private final UserAccountRepository accountRepository;
    private final TicketQueryService ticketQueryService;
    private final TicketMapper ticketMapper;

    public CustomerPortalService(UserAccountRepository accountRepository, TicketQueryService ticketQueryService,
                                 TicketMapper ticketMapper) {
        this.accountRepository = accountRepository;
        this.ticketQueryService = ticketQueryService;
        this.ticketMapper = ticketMapper;
    }

    @Transactional(readOnly = true)
    public CustomerPortalSnapshot dashboard(String username, int page) {
        UserAccount account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบบัญชีผู้ใช้"));
        if (account.getRole() != AccountRole.CUSTOMER || account.getCustomer() == null) {
            throw new org.springframework.security.access.AccessDeniedException("บัญชีนี้ไม่มีสิทธิ์เข้าหน้าลูกค้า");
        }
        Page<TicketResponse> tickets = ticketQueryService.findByCustomer(account.getCustomer().getId(),
                        PageRequest.of(Math.max(0, page), 20, Sort.by(Sort.Direction.DESC, "pawnDate")))
                .map(ticketMapper::toResponse);
        return new CustomerPortalSnapshot(account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName(), tickets);
    }

    public record CustomerPortalSnapshot(String customerName, Page<TicketResponse> tickets) {}
}
