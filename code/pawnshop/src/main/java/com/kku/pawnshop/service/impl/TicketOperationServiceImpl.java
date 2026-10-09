package com.kku.pawnshop.service.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kku.pawnshop.domain.entity.InterestPolicy;
import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.state.TicketState;
import com.kku.pawnshop.domain.state.TicketStateFactory;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.exception.InvalidTicketOperationException;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import com.kku.pawnshop.repository.PawnTicketRepository;
import com.kku.pawnshop.service.LedgerService;
import com.kku.pawnshop.service.TicketOperationService;
import com.kku.pawnshop.service.interest.InterestCalculator;

@Service
@Transactional
public class TicketOperationServiceImpl implements TicketOperationService {
	private final PawnTicketRepository ticketRepository;
	private final TicketStateFactory stateFactory;
	private final InterestCalculator interestCalculator;
	private final LedgerService ledgerService;

	public TicketOperationServiceImpl(PawnTicketRepository ticketRepository, TicketStateFactory stateFactory,
			InterestCalculator interestCalculator, LedgerService ledgerService) {
		this.ticketRepository = ticketRepository;
		this.stateFactory = stateFactory;
		this.interestCalculator = interestCalculator;
		this.ledgerService = ledgerService;
	}

	@Override
	public PawnTicket openTicket(Long customerId, Long pledgedItemId, Money requestedPrincipal, Long employeeId) {
		return null;
	}

	@Override
	public PawnTicket renewInterest(Long ticketId, LocalDate paymentDate, Long employeeId) {
		PawnTicket ticket = loadTicket(ticketId);
		TicketState state = stateFactory.stateOf(ticket.getStatus());

		if (!state.canRenew()) {
			throw new InvalidTicketOperationException(ticket.getTicketNumber(), ticket.getStatus(), "ต่อดอก");
		}

		Money interest = interestCalculator.accruedInterest(ticket, paymentDate);
		ledgerService.recordInterestPayment(ticket, interest, ticket.getInterestPaidUntil(), paymentDate, employeeId);

		InterestPolicy policy = ticket.getInterestPolicy();
		LocalDate newDueDate = paymentDate.plusMonths(policy.getRedemptionMonths());

		ticket.setInterestPaidUntil(paymentDate);
		ticket.setDueDate(newDueDate);
		ticket.setGraceEndDate(newDueDate.plusDays(policy.getGraceDays()));
		ticket.setStatus(state.nextAfterRenew());

		return ticketRepository.save(ticket);
	}

	@Override
	public PawnTicket redeem(Long ticketId, LocalDate paymentDate, Long employeeId) {
		return null;
	}

	private PawnTicket loadTicket(Long ticketId) {
		return ticketRepository.findById(ticketId)
				.orElseThrow(() -> new ResourceNotFoundException("ตั๋วจำนำ", ticketId));
	}
}
