package com.kku.pawnshop.domain.state;

import com.kku.pawnshop.domain.enums.TicketStatus;
import org.springframework.stereotype.Component;

/** ไถ่ถอนแล้ว เป็นสถานะสุดท้าย ทำอะไรต่อไม่ได้ */
@Component
public class RedeemedState implements TicketState {

    @Override public TicketStatus status() { return TicketStatus.REDEEMED; }

    @Override public boolean canRenew() { return false; }
    @Override public boolean canRedeem() { return false; }
    @Override public boolean canForfeit() { return false; }
    @Override public boolean canSell() { return false; }

    @Override public TicketStatus nextAfterRenew() { return TicketStatus.REDEEMED; }
    @Override public TicketStatus nextAfterRedeem() { return TicketStatus.REDEEMED; }
    @Override public TicketStatus nextAfterForfeit() { return TicketStatus.REDEEMED; }
    @Override public TicketStatus nextAfterSell() { return TicketStatus.REDEEMED; }

    @Override public String displayName() { return "ไถ่ถอนแล้ว"; }
}
