package com.kku.pawnshop.domain.state;

import com.kku.pawnshop.domain.enums.TicketStatus;

/**
 * State Pattern — พฤติกรรมของตั๋วในแต่ละสถานะ
 *
 * ข้อบังคับสำคัญตามใบงาน (LSP): ห้าม implementation ตัวใดโยน exception
 * ทุกตัวต้องตอบได้ทุกเมธอด เอาไปแทนกันได้โดยไม่พัง
 *
 * วิธีออกแบบให้เป็นแบบนั้น: แยกเป็น 2 กลุ่ม
 *   1) canXxx()  — ถามว่าทำได้ไหม ทุกสถานะตอบ true/false ได้เสมอ
 *   2) nextAfterXxx() — ถ้าทำแล้วจะไปสถานะไหน สถานะที่ทำไม่ได้คืนสถานะเดิม
 *
 * การปฏิเสธคำสั่งเป็นหน้าที่ของ Service Layer ที่จะเช็ค canXxx() ก่อน
 * แล้วโยน InvalidTicketOperationException เอง ไม่ใช่หน้าที่ของ State
 */
public interface TicketState {

    TicketStatus status();

    boolean canRenew();

    boolean canRedeem();

    boolean canForfeit();

    boolean canSell();

    TicketStatus nextAfterRenew();

    TicketStatus nextAfterRedeem();

    TicketStatus nextAfterForfeit();

    TicketStatus nextAfterSell();

    /** ข้อความอธิบายสถานะสำหรับแสดงบนหน้าจอ */
    String displayName();
}
