package com.bysmo.serenity.notification.dto;

import com.bysmo.serenity.notification.entity.enums.NotificationChannel;
import com.bysmo.serenity.notification.entity.enums.NotificationType;
import com.bysmo.serenity.notification.entity.enums.RecipientType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendNotificationRequest {

    @NotNull(message = "Le type de notification est obligatoire")
    private NotificationType type;

    @NotNull(message = "L'ID du destinataire est obligatoire")
    private UUID recipientId;

    @NotNull(message = "Le type de destinataire est obligatoire")
    private RecipientType recipientType;

    @NotNull(message = "Le canal de notification est obligatoire")
    private NotificationChannel channel;

    private String subject;

    @NotBlank(message = "Le contenu est obligatoire")
    private String content;
}
