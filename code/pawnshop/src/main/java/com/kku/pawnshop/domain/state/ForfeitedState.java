package com.kku.pawnshop.domain.state;

import com.kku.pawnshop.domain.enums.TicketStatus;
import org.springframework.stereotype.Component;

/** หลุดจำนำ ทรัพย์ตกเป็นของร้าน ไถ่ถอนไม่ได้แล้ว แต่นำออกขายได้ */
@Component
public class ForfeitedState implements TicketState {

    @Override public TicketStatus status() { return TicketStatus.FORFEITED; }

    @Override public boolean canRenew() { return false; }
    @Override public boolean canRedeem() { return false; }
    @Override public boolean canForfeit() { return false; }
    @Override public boolean canSell() { return true; }

    @Override public TicketStatus nextAfterRenew() { return TicketStatus.FORFEITED; }
    @Override public TicketStatus nextAfterRedeem() { return TicketStatus.FORFEITED; }
    @Override public TicketStatus nextAfterForfeit() { return TicketStatus.FORFEITED; }
    @Override public TicketStatus nextAfterSell() { return TicketStatus.SOLD; }

    @Override public String displayName() { return "หลุดจำนำ"; }
}
