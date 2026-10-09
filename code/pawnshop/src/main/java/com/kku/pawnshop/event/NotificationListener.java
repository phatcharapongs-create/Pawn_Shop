package com.kku.pawnshop.event;

import com.kku.pawnshop.domain.entity.NotificationLog;
import com.kku.pawnshop.domain.enums.NotificationChannel;
import com.kku.pawnshop.domain.enums.NotificationStatus;
import com.kku.pawnshop.repository.NotificationLogRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationListener {

    private final NotificationLogRepository notificationLogRepository;

    public NotificationListener(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    @EventListener
    public void handleTicketRedeemed(TicketRedeemedEvent event) {
        saveLog("TICKET_REDEEMED", "Ticket redeemed successfully for ticket ID: " + event.getTicketId());
    }

    @EventListener
    public void handleTicketForfeited(TicketForfeitedEvent event) {
        saveLog("TICKET_FORFEITED", "Ticket forfeited for ticket ID: " + event.getTicketId());
    }

    private void saveLog(String eventType, String message) {
        NotificationLog log = new NotificationLog();
        log.setMessage(message);
        log.setChannel(NotificationChannel.SYSTEM); // หรือปรับตาม Enum ในโปรเจกต์
        log.setStatus(NotificationStatus.SENT);
        log.setSentAt(LocalDateTime.now());
        notificationLogRepository.save(log);
    }
}