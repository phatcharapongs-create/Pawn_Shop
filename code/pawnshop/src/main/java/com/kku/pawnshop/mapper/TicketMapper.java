package com.kku.pawnshop.mapper;

import org.springframework.stereotype.Component;

import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.state.TicketState;
import com.kku.pawnshop.domain.state.TicketStateFactory;
import com.kku.pawnshop.dto.response.TicketResponse;

/**
 * แปลง PawnTicket → TicketResponse
 *
 * ใส่ canRenew / canRedeem ไปด้วย หน้าเว็บจะได้ซ่อนปุ่มที่ทำไม่ได้
 * โดยไม่ต้องเขียนเงื่อนไขสถานะซ้ำใน HTML (ถาม State ที่เดียว)
 */
@Component
public class TicketMapper {

    private final TicketStateFactory stateFactory;

    public TicketMapper(TicketStateFactory stateFactory) {
        this.stateFactory = stateFactory;
    }

    public TicketResponse toResponse(PawnTicket t) {
        TicketState state = stateFactory.stateOf(t.getStatus());
        return new TicketResponse(
                t.getId(),
                t.getTicketNumber(),
                t.getCustomer() != null ? t.getCustomer().getId() : null,
                t.getCustomer() != null ? t.getCustomer().getFirstName() + " " + t.getCustomer().getLastName() : null,
                t.getPledgedItem() != null ? t.getPledgedItem().getId() : null,
                t.getPledgedItem() != null ? t.getPledgedItem().getDescription() : null,
                t.getPrincipal() != null ? t.getPrincipal().getAmount() : null,
                t.getPawnDate(),
                t.getDueDate(),
                t.getGraceEndDate(),
                t.getInterestPaidUntil(),
                t.getStatus().name(),
                state.displayName(),
                state.canRenew(),
                state.canRedeem());
    }
}
