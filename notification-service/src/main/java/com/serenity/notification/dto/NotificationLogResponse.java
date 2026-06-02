package com.serenity.notification.dto;

import com.serenity.notification.entity.enums.NotificationChannel;
import com.serenity.notification.entity.enums.NotificationStatus;
import com.serenity.notification.entity.enums.NotificationType;
import com.serenity.notification.entity.enums.RecipientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLogResponse {

    private UUID id;
    private NotificationType type;
    private UUID recipientId;
    private RecipientType recipientType;
    private NotificationChannel channel;
    private String subject;
    private String content;
    private NotificationStatus status;
    private String errorMessage;
    private String providerReference;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
