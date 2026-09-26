package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.NotificationLog;
import com.kku.pawnshop.domain.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** เจ้าของ: สมาชิก E */
@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    List<NotificationLog> findByTicketId(Long ticketId);

    List<NotificationLog> findByStatus(NotificationStatus status);
}
