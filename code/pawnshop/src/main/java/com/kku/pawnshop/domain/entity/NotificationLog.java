package com.kku.pawnshop.domain.entity;

import com.kku.pawnshop.domain.enums.NotificationChannel;
import com.kku.pawnshop.domain.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * บันทึกการแจ้งเตือน — เจ้าของ: สมาชิก E
 *
 * Observer Pattern: Service ที่ทำธุรกรรมไม่รู้จักคลาสนี้เลย
 * มันแค่ publish event ออกไป แล้ว NotificationListener เป็นคนเขียนลงตารางนี้
 * ถ้าอนาคตจะส่ง SMS จริง แค่เพิ่ม listener ตัวใหม่ ไม่ต้องแก้ service เดิม
 */
@Entity
@Table(name = "notification_log", indexes = {
        @Index(name = "idx_notification_ticket", columnList = "ticket_id"),
        @Index(name = "idx_notification_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private PawnTicket ticket;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
