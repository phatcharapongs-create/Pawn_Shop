package com.kku.pawnshop.domain.state;

import com.kku.pawnshop.domain.enums.TicketStatus;
import org.springframework.stereotype.Component;

/** ตั๋วอยู่ในกำหนดไถ่ถอน ทำได้ทั้งต่อดอกและไถ่ถอน */
@Component
public class ActiveState implements TicketState {

    @Override public TicketStatus status() { return TicketStatus.ACTIVE; }

    @Override public boolean canRenew() { return true; }
    @Override public boolean canRedeem() { return true; }
    @Override public boolean canForfeit() { return false; }
    @Override public boolean canSell() { return false; }

    @Override public TicketStatus nextAfterRenew() { return TicketStatus.ACTIVE; }
    @Override public TicketStatus nextAfterRedeem() { return TicketStatus.REDEEMED; }
    @Override public TicketStatus nextAfterForfeit() { return TicketStatus.ACTIVE; }
    @Override public TicketStatus nextAfterSell() { return TicketStatus.ACTIVE; }

    @Override public String displayName() { return "ในกำหนด"; }
}
