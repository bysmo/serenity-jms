package com.bysmo.serenity.notification.repository;

import com.bysmo.serenity.notification.entity.NotificationLog;
import com.bysmo.serenity.notification.entity.enums.NotificationStatus;
import com.bysmo.serenity.notification.entity.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    List<NotificationLog> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);

    List<NotificationLog> findByStatusOrderByCreatedAtDesc(NotificationStatus status);

    List<NotificationLog> findByTypeOrderByCreatedAtDesc(NotificationType type);

    List<NotificationLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
}
