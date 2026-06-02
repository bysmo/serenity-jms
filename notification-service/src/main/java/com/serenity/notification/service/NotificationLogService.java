package com.serenity.notification.service;

import com.serenity.notification.entity.NotificationLog;
import com.serenity.notification.entity.enums.NotificationChannel;
import com.serenity.notification.entity.enums.NotificationStatus;
import com.serenity.notification.entity.enums.NotificationType;
import com.serenity.notification.entity.enums.RecipientType;
import com.serenity.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;

    @Transactional
    public NotificationLog log(NotificationType type, UUID recipientId, RecipientType recipientType,
                               NotificationChannel channel, String subject, String content,
                               NotificationStatus status, String errorMessage) {
        NotificationLog notificationLog = NotificationLog.builder()
                .type(type)
                .recipientId(recipientId)
                .recipientType(recipientType)
                .channel(channel)
                .subject(subject)
                .content(content)
                .status(status)
                .errorMessage(errorMessage)
                .sentAt(status == NotificationStatus.SENT ? LocalDateTime.now() : null)
                .build();

        NotificationLog saved = notificationLogRepository.save(notificationLog);
        log.info("Notification log saved: id={}, type={}, channel={}, status={}, recipient={}",
                saved.getId(), type, channel, status, recipientId);
        return saved;
    }

    @Transactional
    public NotificationLog logSuccess(NotificationType type, UUID recipientId, RecipientType recipientType,
                                       NotificationChannel channel, String subject, String content,
                                       String providerReference) {
        NotificationLog notificationLog = NotificationLog.builder()
                .type(type)
                .recipientId(recipientId)
                .recipientType(recipientType)
                .channel(channel)
                .subject(subject)
                .content(content)
                .status(NotificationStatus.SENT)
                .providerReference(providerReference)
                .sentAt(LocalDateTime.now())
                .build();

        NotificationLog saved = notificationLogRepository.save(notificationLog);
        log.info("Notification sent successfully: id={}, type={}, channel={}, recipient={}",
                saved.getId(), type, channel, recipientId);
        return saved;
    }

    @Transactional
    public NotificationLog logFailure(NotificationType type, UUID recipientId, RecipientType recipientType,
                                       NotificationChannel channel, String subject, String content,
                                       String errorMessage) {
        NotificationLog notificationLog = NotificationLog.builder()
                .type(type)
                .recipientId(recipientId)
                .recipientType(recipientType)
                .channel(channel)
                .subject(subject)
                .content(content)
                .status(NotificationStatus.FAILED)
                .errorMessage(errorMessage)
                .build();

        NotificationLog saved = notificationLogRepository.save(notificationLog);
        log.error("Notification failed: id={}, type={}, channel={}, recipient={}, error={}",
                saved.getId(), type, channel, recipientId, errorMessage);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<NotificationLog> getLogs(UUID recipientId) {
        return notificationLogRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    @Transactional(readOnly = true)
    public List<NotificationLog> getLogsByStatus(NotificationStatus status) {
        return notificationLogRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional(readOnly = true)
    public List<NotificationLog> getLogsByType(NotificationType type) {
        return notificationLogRepository.findByTypeOrderByCreatedAtDesc(type);
    }

    @Transactional(readOnly = true)
    public List<NotificationLog> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return notificationLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<NotificationLog> getAllLogs() {
        return notificationLogRepository.findAll();
    }
}
