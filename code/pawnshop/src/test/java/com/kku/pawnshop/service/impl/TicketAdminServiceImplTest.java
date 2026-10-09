package com.kku.pawnshop.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.kku.pawnshop.domain.entity.Customer;
import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.enums.TicketStatus;
import com.kku.pawnshop.domain.state.ActiveState;
import com.kku.pawnshop.domain.state.ForfeitedState;
import com.kku.pawnshop.domain.state.GraceState;
import com.kku.pawnshop.domain.state.RedeemedState;
import com.kku.pawnshop.domain.state.SeizedState;
import com.kku.pawnshop.domain.state.SoldState;
import com.kku.pawnshop.domain.state.TicketStateFactory;
import com.kku.pawnshop.event.TicketForfeitedEvent;
import com.kku.pawnshop.exception.InvalidTicketOperationException;
import com.kku.pawnshop.repository.PawnTicketRepository;

@ExtendWith(MockitoExtension.class)
class TicketAdminServiceImplTest {

    @Mock private PawnTicketRepository ticketRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private TicketAdminServiceImpl service;

    @BeforeEach
    void setUp() {
        TicketStateFactory stateFactory = new TicketStateFactory(List.of(
                new ActiveState(), new GraceState(), new RedeemedState(),
                new ForfeitedState(), new SoldState(), new SeizedState()));
        service = new TicketAdminServiceImpl(ticketRepository, stateFactory, eventPublisher);
    }

    @Test
    void seize_activeTicket_becomesSeized() {
        PawnTicket ticket = ticket(TicketStatus.ACTIVE);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        PawnTicket result = service.seize(1L, "ตำรวจแจ้งอายัด", null);

        assertEquals(TicketStatus.SEIZED, result.getStatus());
        assertEquals("ตำรวจแจ้งอายัด", result.getSeizedReason());
    }

    @Test
    void seize_redeemedTicket_throws() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(TicketStatus.REDEEMED)));

        assertThrows(InvalidTicketOperationException.class, () -> service.seize(1L, "เหตุผล", null));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void releaseSeizure_notYetDue_returnsToActive() {
        PawnTicket ticket = ticket(TicketStatus.SEIZED);
        ticket.setDueDate(LocalDate.now().plusMonths(1));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        PawnTicket result = service.releaseSeizure(1L, null);

        assertEquals(TicketStatus.ACTIVE, result.getStatus());
        assertNull(result.getSeizedReason());
    }

    @Test
    void releaseSeizure_ticketNotSeized_throws() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(TicketStatus.ACTIVE)));

        assertThrows(InvalidTicketOperationException.class, () -> service.releaseSeizure(1L, null));
    }

    @Test
    void forfeitExpired_graceTickets_becomeForfeitedAndPublishEvent() {
        LocalDate asOf = LocalDate.of(2026, 10, 1);
        PawnTicket expired = ticket(TicketStatus.GRACE);
        when(ticketRepository.findByStatusAndGraceEndDateBefore(TicketStatus.GRACE, asOf))
                .thenReturn(List.of(expired));
        when(ticketRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<PawnTicket> result = service.forfeitExpired(asOf);

        assertEquals(TicketStatus.FORFEITED, result.get(0).getStatus());
        verify(eventPublisher).publishEvent(any(TicketForfeitedEvent.class));
    }

    @Test
    void promoteOverdueToGrace_activeOverdue_becomesGrace() {
        LocalDate asOf = LocalDate.of(2026, 10, 1);
        PawnTicket overdue = ticket(TicketStatus.ACTIVE);
        when(ticketRepository.findByStatusAndDueDateBefore(TicketStatus.ACTIVE, asOf))
                .thenReturn(List.of(overdue));
        when(ticketRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<PawnTicket> result = service.promoteOverdueToGrace(asOf);

        assertEquals(TicketStatus.GRACE, result.get(0).getStatus());
    }

    private PawnTicket ticket(TicketStatus status) {
        Customer customer = new Customer();
        customer.setId(10L);
        PledgedItem item = new PledgedItem();
        item.setId(20L);

        PawnTicket t = new PawnTicket();
        t.setId(1L);
        t.setTicketNumber("PT000001");
        t.setCustomer(customer);
        t.setPledgedItem(item);
        t.setStatus(status);
        return t;
    }
}
