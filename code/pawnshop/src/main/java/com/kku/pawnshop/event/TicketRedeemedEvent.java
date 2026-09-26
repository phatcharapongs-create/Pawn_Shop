package com.kku.pawnshop.event;

import com.kku.pawnshop.domain.vo.Money;

/**
 * Observer Pattern ผ่าน Spring ApplicationEvent
 *
 * TicketOperationService แค่ publish อีเวนต์นี้ออกไป โดยไม่รู้จัก
 * NotificationListener เลย ทำให้เพิ่มการแจ้งเตือนช่องทางใหม่ได้
 * โดยไม่ต้องแก้ service (OCP + ลด coupling)
 */
public record TicketRedeemedEvent(Long ticketId, String ticketNumber, Long customerId, Money totalPaid) {
}
