package com.kku.pawnshop.service.impl;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import com.kku.pawnshop.repository.PawnTicketRepository;
import com.kku.pawnshop.service.TicketQueryService;
import com.kku.pawnshop.service.interest.InterestCalculator;

@Service
@Transactional(readOnly = true)
public class TicketQueryServiceImpl implements TicketQueryService {
	private final PawnTicketRepository ticketRepository;
	private final InterestCalculator interestCalculator;

	public TicketQueryServiceImpl(PawnTicketRepository ticketRepository, InterestCalculator interestCalculator) {
		this.ticketRepository = ticketRepository;
		this.interestCalculator = interestCalculator;
	}

	@Override
	public PawnTicket findById(Long id) {
		return ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ตั๋วจำนำ", id));

	}

	@Override
	public PawnTicket findByTicketNumber(String ticketNumber) {
		return ticketRepository.findByTicketNumber(ticketNumber)
				.orElseThrow(() -> new ResourceNotFoundException("ไม่พบตั๋วจำนำเลขที่" + ticketNumber));
	}

	@Override
	public Page<PawnTicket> findAll(Pageable pageable) {
		return ticketRepository.findAll(pageable);
	}

	@Override
	public Page<PawnTicket> findByCustomer(Long customerId, Pageable pageable) {
		return ticketRepository.findByCustomerId(customerId, pageable);
	}

	@Override
	public Money quoteRedemptionAmount(Long ticketId, LocalDate asOf) {
		PawnTicket ticket = findById(ticketId);
		return interestCalculator.redemptionAmount(ticket, asOf);
	}

	@Override
	public Money quoteRenewalInterest(Long ticketId, LocalDate asOf) {
		PawnTicket ticket = findById(ticketId);
		return interestCalculator.accruedInterest(ticket, asOf);
	}

}
