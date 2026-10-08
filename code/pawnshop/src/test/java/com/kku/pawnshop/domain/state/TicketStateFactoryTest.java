package com.kku.pawnshop.domain.state;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.kku.pawnshop.domain.enums.TicketStatus;

import java.util.List;

public class TicketStateFactoryTest {
	@Test
	void stateOf_activeReturnsActiveState() {
		TicketStateFactory factory = factoryWithAllStates();
		
		TicketState result = factory.stateOf(TicketStatus.ACTIVE);
		
		assertEquals(TicketStatus.ACTIVE, result.status());
	}
	
	@Test 
	void stateOf_everyStatusHasMatchingState() {
		TicketStateFactory factory = factoryWithAllStates();
		
		for (TicketStatus status : TicketStatus.values()) {
		TicketState result = factory.stateOf(status);
		
		assertEquals(status, result.status());
		}
	}
	
	@Test
	void stateOf_missingStateThrowException() {
		TicketStateFactory factory = new TicketStateFactory(List.of(new ActiveState(), 
				new GraceState(), new RedeemedState(), new ForfeitedState()));
		
		
		assertThrows(IllegalStateException.class, () -> factory.stateOf(TicketStatus.SOLD));
		
	}
	private TicketStateFactory factoryWithAllStates() {
		return new TicketStateFactory(List.of(new ActiveState(), 
				new GraceState(), new RedeemedState(), new ForfeitedState(), new SeizedState(), new SoldState()));
	}
}
