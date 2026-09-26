package com.kku.pawnshop.domain.state;

import com.kku.pawnshop.domain.enums.TicketStatus;
import org.springframework.stereotype.Component;

/** จำหน่ายทรัพย์แล้ว เป็นสถานะสุดท้าย */
@Component
public class SoldState implements TicketState {

    @Override public TicketStatus status() { return TicketStatus.SOLD; }

    @Override public boolean canRenew() { return false; }
    @Override public boolean canRedeem() { return false; }
    @Override public boolean canForfeit() { return false; }
    @Override public boolean canSell() { return false; }

    @Override public TicketStatus nextAfterRenew() { return TicketStatus.SOLD; }
    @Override public TicketStatus nextAfterRedeem() { return TicketStatus.SOLD; }
    @Override public TicketStatus nextAfterForfeit() { return TicketStatus.SOLD; }
    @Override public TicketStatus nextAfterSell() { return TicketStatus.SOLD; }

    @Override public String displayName() { return "จำหน่ายแล้ว"; }
}
