package com.kku.pawnshop.domain.state;

import com.kku.pawnshop.domain.enums.TicketStatus;
import org.springframework.stereotype.Component;

/** เลยกำหนดแล้วแต่ยังอยู่ในช่วงผ่อนผัน ยังไถ่ถอนหรือต่อดอกได้ และหลุดจำนำได้ */
@Component
public class GraceState implements TicketState {

    @Override public TicketStatus status() { return TicketStatus.GRACE; }

    @Override public boolean canRenew() { return true; }
    @Override public boolean canRedeem() { return true; }
    @Override public boolean canForfeit() { return true; }
    @Override public boolean canSell() { return false; }

    /** ต่อดอกในช่วงผ่อนผันแล้วกลับไปเป็นตั๋วปกติ */
    @Override public TicketStatus nextAfterRenew() { return TicketStatus.ACTIVE; }
    @Override public TicketStatus nextAfterRedeem() { return TicketStatus.REDEEMED; }
    @Override public TicketStatus nextAfterForfeit() { return TicketStatus.FORFEITED; }
    @Override public TicketStatus nextAfterSell() { return TicketStatus.GRACE; }

    @Override public String displayName() { return "ผ่อนผัน"; }
}
