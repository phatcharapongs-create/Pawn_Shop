package com.kku.pawnshop.exception;

import com.kku.pawnshop.domain.enums.TicketStatus;

/**
 * ทำธุรกรรมกับตั๋วที่อยู่ในสถานะที่ทำไม่ได้ เช่น ไถ่ถอนตั๋วที่ไถ่ไปแล้ว
 * GlobalExceptionHandler จะแปลงเป็น HTTP 409 Conflict
 *
 * สังเกตว่า exception นี้ถูกโยนจาก Service Layer ไม่ใช่จาก TicketState
 * เพราะ State ต้องเอาไปแทนกันได้โดยไม่พัง ตามข้อกำหนด LSP ในใบงาน
 */
public class InvalidTicketOperationException extends RuntimeException {

    public InvalidTicketOperationException(String ticketNumber, TicketStatus status, String operation) {
        super("ตั๋วเลขที่ " + ticketNumber + " อยู่ในสถานะ " + status + " จึง" + operation + "ไม่ได้");
    }
}
