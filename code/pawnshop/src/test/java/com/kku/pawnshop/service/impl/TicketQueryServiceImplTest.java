package com.kku.pawnshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import com.kku.pawnshop.repository.PawnTicketRepository;
import com.kku.pawnshop.service.interest.InterestCalculator;

@ExtendWith(MockitoExtension.class)
public class TicketQueryServiceImplTest {
	@Mock
	private PawnTicketRepository ticketRepository;

	@Mock
	private InterestCalculator interestCalculator;

	@InjectMocks
	private TicketQueryServiceImpl service;

	@Test
	void findById_existingTicket_returnsTicket() {
		PawnTicket ticket = new PawnTicket();
		when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

		PawnTicket result = service.findById(1L);

		assertSame(ticket, result);
	}
	@Test
	void findById_missingTicket_throwNotFound() {
		when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
		
		assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
	}
}
