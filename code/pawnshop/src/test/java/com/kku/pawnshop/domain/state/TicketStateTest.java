package com.kku.pawnshop.domain.state;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.kku.pawnshop.domain.enums.TicketStatus;

public class TicketStateTest {

	@Test
	void activeState_canRedeemButCannotSell() {
		TicketState state = new ActiveState();
		
		assertTrue(state.canRedeem());
		assertFalse(state.canSell());
	}
	
	@Test
	void graceState_RenewGoesBackToActive() {
		TicketState state = new GraceState();
		
		assertEquals(TicketStatus.ACTIVE, state.nextAfterRenew());

	}
	@Test
	void redeemState_cannotDoAnything() {
		TicketState state = new RedeemedState();
		
		assertFalse(state.canRenew());
		assertFalse(state.canRedeem());
		assertFalse(state.canForfeit());
		assertFalse(state.canSell());
	}
	@Test
	void forfeitState_cannotRedeemButCanSell() {
		TicketState state = new ForfeitedState();
		
		assertTrue(state.canSell());
		assertFalse(state.canRenew());
		assertFalse(state.canRedeem());
		assertFalse(state.canForfeit());
	}
	@Test
	void forfeitState_sellGoesToSold() {
		TicketState state = new ForfeitedState();
		
		assertEquals(TicketStatus.SOLD, state.nextAfterSell());
	}
	@Test
	void seizedState_cannotDoAnything() {
		TicketState state = new SeizedState();
		
		assertFalse(state.canRenew());
		assertFalse(state.canRedeem());
		assertFalse(state.canForfeit());
		assertFalse(state.canSell());
	}
	@Test
	void seizedState_statusCannotChange() {
		TicketState state = new SeizedState();
		
		assertEquals(TicketStatus.SEIZED, state.nextAfterRedeem() );
	}
}
