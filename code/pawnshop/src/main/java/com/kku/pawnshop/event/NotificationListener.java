package com.kku.pawnshop.event;

import com.kku.pawnshop.domain.entity.NotificationLog;
import com.kku.pawnshop.domain.enums.NotificationChannel;
import com.kku.pawnshop.domain.enums.NotificationStatus;
import com.kku.pawnshop.repository.NotificationLogRepository;
import com.kku.pawnshop.repository.PawnTicketRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationListener {

    private final NotificationLogRepository notificationLogRepository;
    private final PawnTicketRepository pawnTicketRepository;

    public NotificationListener(NotificationLogRepository notificationLogRepository,
                                PawnTicketRepository pawnTicketRepository) {
        this.notificationLogRepository = notificationLogRepository;
        this.pawnTicketRepository = pawnTicketRepository;
    }

    @EventListener
    public void handleTicketRedeemed(TicketRedeemedEvent event) {
        // จุดที่ 1: เปลี่ยนเป็น event.ticketId() และใช้ IN_APP
        saveLog(event.ticketId(), "Ticket redeemed successfully for ticket ID: " + event.ticketId());
    }

    @EventListener
    public void handleTicketForfeited(TicketForfeitedEvent event) {
        // จุดที่ 1: เปลี่ยนเป็น event.ticketId() และใช้ IN_APP
        saveLog(event.ticketId(), "Ticket forfeited for ticket ID: " + event.ticketId());
    }

    private void saveLog(Long ticketId, String message) {
        NotificationLog log = new NotificationLog();
        
        // จุดแนะนำเพิ่มเติม: ผูก PawnTicket กับ NotificationLog
        pawnTicketRepository.findById(ticketId).ifPresent(log::setTicket);

        log.setMessage(message);
        log.setChannel(NotificationChannel.IN_APP); // จุดที่ 2: เปลี่ยนเป็น IN_APP
        log.setStatus(NotificationStatus.SENT);
        log.setSentAt(LocalDateTime.now());
        
        notificationLogRepository.save(log);
    }
}