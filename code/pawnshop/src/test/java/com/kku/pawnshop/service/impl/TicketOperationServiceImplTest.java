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

import com.kku.pawnshop.domain.entity.Appraisal;
import com.kku.pawnshop.domain.entity.Customer;
import com.kku.pawnshop.domain.entity.InterestPolicy;
import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.enums.LedgerEntryType;
import com.kku.pawnshop.domain.enums.TicketStatus;
import com.kku.pawnshop.domain.state.ActiveState;
import com.kku.pawnshop.domain.state.ForfeitedState;
import com.kku.pawnshop.domain.state.GraceState;
import com.kku.pawnshop.domain.state.RedeemedState;
import com.kku.pawnshop.domain.state.SeizedState;
import com.kku.pawnshop.domain.state.SoldState;
import com.kku.pawnshop.domain.state.TicketStateFactory;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.event.TicketRedeemedEvent;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import com.kku.pawnshop.exception.InvalidTicketOperationException;
import com.kku.pawnshop.repository.EmployeeRepository;
import com.kku.pawnshop.repository.InterestPolicyRepository;
import com.kku.pawnshop.repository.PawnTicketRepository;
import com.kku.pawnshop.service.AppraisalService;
import com.kku.pawnshop.service.CustomerService;
import com.kku.pawnshop.service.LedgerService;
import com.kku.pawnshop.service.PledgedItemService;
import com.kku.pawnshop.service.interest.InterestCalculator;

/**
 * เทส TicketOperationServiceImpl
 *
 * ใช้ TicketStateFactory ตัวจริง (ไม่ mock) เพราะอยากเทสว่า service
 * ต่อสายกับ State pattern ถูกต้อง ส่วนอย่างอื่นเป็นของปลอมทั้งหมด
 */
@ExtendWith(MockitoExtension.class)
class TicketOperationServiceImplTest {

    @Mock private PawnTicketRepository ticketRepository;
    @Mock private InterestCalculator interestCalculator;
    @Mock private LedgerService ledgerService;
    @Mock private CustomerService customerService;
    @Mock private PledgedItemService pledgedItemService;
    @Mock private AppraisalService appraisalService;
    @Mock private InterestPolicyRepository policyRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private TicketOperationServiceImpl service;

    private final LocalDate today = LocalDate.of(2026, 10, 1);

    @BeforeEach
    void setUp() {
        TicketStateFactory stateFactory = new TicketStateFactory(List.of(
                new ActiveState(), new GraceState(), new RedeemedState(),
                new ForfeitedState(), new SoldState(), new SeizedState()));
        service = new TicketOperationServiceImpl(ticketRepository, stateFactory, interestCalculator,
                ledgerService, customerService, pledgedItemService, appraisalService,
                policyRepository, employeeRepository, eventPublisher);
    }

    // ---------- ต่อดอก ----------

    @Test
    void renewInterest_activeTicket_extendsDueDateAndRecordsLedger() {
        PawnTicket ticket = ticketWithStatus(TicketStatus.ACTIVE);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(interestCalculator.accruedInterest(ticket, today)).thenReturn(Money.of(150));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        PawnTicket result = service.renewInterest(1L, today, null);

        assertEquals(today, result.getInterestPaidUntil());
        assertEquals(today.plusMonths(4), result.getDueDate());
        assertEquals(today.plusMonths(4).plusDays(30), result.getGraceEndDate());
        assertEquals(TicketStatus.ACTIVE, result.getStatus());
        verify(ledgerService).recordEntry(eq(ticket), eq(LedgerEntryType.INTEREST_PAYMENT),
                eq(Money.zero()), eq(Money.of(150)), eq(Money.of(150)),
                eq(LocalDate.of(2026, 6, 1)), eq(today), isNull(), anyString());
    }

    @Test
    void renewInterest_graceTicket_goesBackToActive() {
        PawnTicket ticket = ticketWithStatus(TicketStatus.GRACE);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(interestCalculator.accruedInterest(ticket, today)).thenReturn(Money.of(150));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        PawnTicket result = service.renewInterest(1L, today, null);

        assertEquals(TicketStatus.ACTIVE, result.getStatus());
    }

    @Test
    void renewInterest_redeemedTicket_throwsAndDoesNotTouchLedger() {
        PawnTicket ticket = ticketWithStatus(TicketStatus.REDEEMED);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(InvalidTicketOperationException.class, () -> service.renewInterest(1L, today, null));

        verifyNoInteractions(ledgerService);
        verify(ticketRepository, never()).save(any());
    }

    // ---------- ไถ่ถอน ----------

    @Test
    void redeem_activeTicket_closesTicketAndPublishesEvent() {
        PawnTicket ticket = ticketWithStatus(TicketStatus.ACTIVE);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(interestCalculator.accruedInterest(ticket, today)).thenReturn(Money.of(300));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        PawnTicket result = service.redeem(1L, today, null);

        assertEquals(TicketStatus.REDEEMED, result.getStatus());
        assertNotNull(result.getClosedAt());
        verify(ledgerService).recordEntry(eq(ticket), eq(LedgerEntryType.REDEMPTION),
                eq(Money.of(5000)), eq(Money.of(300)), eq(Money.of(5300)),
                any(), eq(today), isNull(), anyString());
        verify(eventPublisher).publishEvent(any(TicketRedeemedEvent.class));
    }

    @Test
    void redeem_seizedTicket_throwsAndPublishesNothing() {
        PawnTicket ticket = ticketWithStatus(TicketStatus.SEIZED);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(InvalidTicketOperationException.class, () -> service.redeem(1L, today, null));

        verifyNoInteractions(ledgerService, eventPublisher);
    }

    // ---------- รับจำนำ ----------

    @Test
    void openTicket_withinLimit_createsActiveTicketAndRecordsPawn() {
        stubOpenTicketDependencies(Money.of(8000));
        when(ticketRepository.save(any(PawnTicket.class))).thenAnswer(inv -> inv.getArgument(0));

        PawnTicket result = service.openTicket(10L, 20L, Money.of(5000), null);

        assertEquals(TicketStatus.ACTIVE, result.getStatus());
        assertEquals(Money.of(5000), result.getPrincipal());
        assertEquals("PT000001", result.getTicketNumber());
        verify(ledgerService).recordEntry(any(), eq(LedgerEntryType.PAWN),
                eq(Money.of(5000)), eq(Money.zero()), eq(Money.of(5000)),
                isNull(), isNull(), isNull(), anyString());
    }

    @Test
    void openTicket_exceedsMaxLoan_throws() {
        stubOpenTicketDependencies(Money.of(3000));

        assertThrows(BusinessRuleViolationException.class,
                () -> service.openTicket(10L, 20L, Money.of(5000), null));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void openTicket_zeroPrincipal_throwsBeforeCallingAnything() {
        assertThrows(BusinessRuleViolationException.class,
                () -> service.openTicket(10L, 20L, Money.zero(), null));

        verifyNoInteractions(customerService, ticketRepository);
    }

    // ---------- ตัวช่วย ----------

    private PawnTicket ticketWithStatus(TicketStatus status) {
        InterestPolicy policy = new InterestPolicy();
        policy.setRedemptionMonths(4);
        policy.setGraceDays(30);

        Customer customer = new Customer();
        customer.setId(10L);

        PawnTicket ticket = new PawnTicket();
        ticket.setId(1L);
        ticket.setTicketNumber("PT000001");
        ticket.setCustomer(customer);
        ticket.setInterestPolicy(policy);
        ticket.setPrincipal(Money.of(5000));
        ticket.setInterestPaidUntil(LocalDate.of(2026, 6, 1));
        ticket.setStatus(status);
        return ticket;
    }

    private void stubOpenTicketDependencies(Money maxLoan) {
        Appraisal appraisal = new Appraisal();
        appraisal.setMaxLoanAmount(maxLoan);

        InterestPolicy policy = new InterestPolicy();
        policy.setRedemptionMonths(4);
        policy.setGraceDays(30);

        when(customerService.findById(10L)).thenReturn(new Customer());
        when(pledgedItemService.findById(20L)).thenReturn(new PledgedItem());
        when(ticketRepository.existsByPledgedItemId(20L)).thenReturn(false);
        when(appraisalService.findByPledgedItemId(20L)).thenReturn(appraisal);
        lenient().when(policyRepository.findEffectiveOn(any())).thenReturn(Optional.of(policy));
        lenient().when(ticketRepository.count()).thenReturn(0L);
    }
}
