package com.kku.pawnshop.domain.state;

import com.kku.pawnshop.domain.enums.TicketStatus;
import org.springframework.stereotype.Component;

/**
 * ถูกอายัดโดยเจ้าพนักงาน ระงับทุกธุรกรรมชั่วคราว
 *
 * สถานะนี้คือเหตุผลหลักที่ต้องใช้ State Pattern แทน if-else
 * เพราะมันแทรกเข้ามาได้จากทั้ง ACTIVE และ GRACE และปิดทุกความสามารถพร้อมกัน
 * ถ้าใช้ if-else จะต้องไปเพิ่มเงื่อนไขในทุกจุดที่เช็คสถานะ
 * แต่แบบนี้แค่เพิ่มคลาสใหม่ 1 คลาสแล้วลงทะเบียนกับ factory
 */
@Component
public class SeizedState implements TicketState {

    @Override public TicketStatus status() { return TicketStatus.SEIZED; }

    @Override public boolean canRenew() { return false; }
    @Override public boolean canRedeem() { return false; }
    @Override public boolean canForfeit() { return false; }
    @Override public boolean canSell() { return false; }

    @Override public TicketStatus nextAfterRenew() { return TicketStatus.SEIZED; }
    @Override public TicketStatus nextAfterRedeem() { return TicketStatus.SEIZED; }
    @Override public TicketStatus nextAfterForfeit() { return TicketStatus.SEIZED; }
    @Override public TicketStatus nextAfterSell() { return TicketStatus.SEIZED; }

    @Override public String displayName() { return "ถูกอายัด"; }
}
