package com.serenity.notification.controller;

import com.serenity.notification.dto.NotificationLogResponse;
import com.serenity.notification.dto.SendNotificationRequest;
import com.serenity.notification.entity.NotificationLog;
import com.serenity.notification.entity.enums.NotificationChannel;
import com.serenity.notification.entity.enums.NotificationStatus;
import com.serenity.notification.entity.enums.NotificationType;
import com.serenity.notification.entity.enums.RecipientType;
import com.serenity.notification.service.EmailService;
import com.serenity.notification.service.NotificationLogService;
import com.serenity.notification.service.PushNotificationService;
import com.serenity.notification.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "API de gestion des notifications")
public class NotificationController {

    private final NotificationLogService notificationLogService;
    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushNotificationService;

    @GetMapping
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    @Operation(summary = "Liste des logs de notification avec filtres")
    public ResponseEntity<List<NotificationLogResponse>> getLogs(
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        List<NotificationLog> logs;

        if (status != null) {
            logs = notificationLogService.getLogsByStatus(status);
        } else if (type != null) {
            logs = notificationLogService.getLogsByType(type);
        } else if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            logs = notificationLogService.getLogsByDateRange(start, end);
        } else {
            logs = notificationLogService.getAllLogs();
        }

        List<NotificationLogResponse> responses = logs.stream()
                .map(this::toNotificationLogResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/send")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    @Operation(summary = "Envoyer une notification manuellement")
    public ResponseEntity<NotificationLogResponse> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {

        switch (request.getChannel()) {
            case EMAIL -> emailService.sendEmailToRecipient(
                    request.getRecipientId().toString(), // Placeholder: real email from service
                    request.getSubject(),
                    request.getContent(),
                    request.getRecipientId(),
                    request.getRecipientType()
            );
            case SMS -> smsService.sendSmsToRecipient(
                    request.getRecipientId().toString(), // Placeholder: real phone from service
                    request.getContent(),
                    request.getRecipientId(),
                    request.getRecipientType()
            );
            case PUSH -> pushNotificationService.sendPushToRecipient(
                    request.getRecipientId().toString(), // Placeholder: real FCM token from service
                    request.getSubject(),
                    request.getContent(),
                    request.getRecipientId(),
                    request.getRecipientType()
            );
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/recipient/{recipientId}")
    @Operation(summary = "Récupérer les logs de notification d'un destinataire")
    public ResponseEntity<List<NotificationLogResponse>> getLogsByRecipient(
            @PathVariable UUID recipientId) {

        List<NotificationLog> logs = notificationLogService.getLogs(recipientId);
        List<NotificationLogResponse> responses = logs.stream()
                .map(this::toNotificationLogResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    private NotificationLogResponse toNotificationLogResponse(NotificationLog log) {
        return NotificationLogResponse.builder()
                .id(log.getId())
                .type(log.getType())
                .recipientId(log.getRecipientId())
                .recipientType(log.getRecipientType())
                .channel(log.getChannel())
                .subject(log.getSubject())
                .content(log.getContent())
                .status(log.getStatus())
                .errorMessage(log.getErrorMessage())
                .providerReference(log.getProviderReference())
                .sentAt(log.getSentAt())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
