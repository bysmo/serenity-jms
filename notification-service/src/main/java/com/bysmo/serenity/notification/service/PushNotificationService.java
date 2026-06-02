package com.bysmo.serenity.notification.service;

import com.bysmo.serenity.notification.config.NotificationProperties;
import com.bysmo.serenity.notification.entity.enums.NotificationChannel;
import com.bysmo.serenity.notification.entity.enums.NotificationType;
import com.bysmo.serenity.notification.entity.enums.RecipientType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final NotificationProperties notificationProperties;
    private final NotificationLogService notificationLogService;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String FCM_API_URL = "https://fcm.googleapis.com/v1/projects/%s/messages:send";

    /**
     * Send a push notification to a specific device via FCM token.
     */
    public void sendPush(String fcmToken, String title, String body) {
        log.info("Sending push notification to FCM token: {}...", fcmToken != null ? fcmToken.substring(0, Math.min(10, fcmToken.length())) : "null");
        try {
            String serviceAccountKeyPath = notificationProperties.getFcm().getServiceAccountKeyPath();
            if (serviceAccountKeyPath == null || serviceAccountKeyPath.isEmpty()) {
                log.warn("FCM service account key path not configured. Push notification not sent.");
                notificationLogService.logFailure(
                        NotificationType.PUSH, null, RecipientType.MEMBRE,
                        NotificationChannel.PUSH, title, body,
                        "FCM service account key not configured"
                );
                return;
            }

            Map<String, Object> message = new HashMap<>();
            Map<String, Object> messageWrapper = new HashMap<>();
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", title);
            notification.put("body", body);

            messageWrapper.put("token", fcmToken);
            messageWrapper.put("notification", notification);
            message.put("message", messageWrapper);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(getAccessToken());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);

            String projectId = extractProjectId(serviceAccountKeyPath);
            String fcmUrl = String.format(FCM_API_URL, projectId);

            String response = restTemplate.postForObject(fcmUrl, request, String.class);
            log.info("Push notification sent successfully. Response: {}", response);

            notificationLogService.logSuccess(
                    NotificationType.PUSH, null, RecipientType.MEMBRE,
                    NotificationChannel.PUSH, title, body, fcmToken
            );
        } catch (Exception e) {
            log.error("Failed to send push notification to FCM token: {}", fcmToken, e);
            notificationLogService.logFailure(
                    NotificationType.PUSH, null, RecipientType.MEMBRE,
                    NotificationChannel.PUSH, title, body, e.getMessage()
            );
        }
    }

    /**
     * Send a push notification to a topic (e.g., all admins).
     */
    public void sendPushToTopic(String topic, String title, String body) {
        log.info("Sending push notification to topic: {}", topic);
        try {
            String serviceAccountKeyPath = notificationProperties.getFcm().getServiceAccountKeyPath();
            if (serviceAccountKeyPath == null || serviceAccountKeyPath.isEmpty()) {
                log.warn("FCM service account key path not configured. Push notification to topic not sent.");
                notificationLogService.logFailure(
                        NotificationType.PUSH, null, RecipientType.USER,
                        NotificationChannel.PUSH, title, body,
                        "FCM service account key not configured"
                );
                return;
            }

            Map<String, Object> message = new HashMap<>();
            Map<String, Object> messageWrapper = new HashMap<>();
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", title);
            notification.put("body", body);

            messageWrapper.put("topic", topic);
            messageWrapper.put("notification", notification);
            message.put("message", messageWrapper);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(getAccessToken());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);

            String projectId = extractProjectId(serviceAccountKeyPath);
            String fcmUrl = String.format(FCM_API_URL, projectId);

            String response = restTemplate.postForObject(fcmUrl, request, String.class);
            log.info("Push notification to topic {} sent successfully. Response: {}", topic, response);

            notificationLogService.logSuccess(
                    NotificationType.PUSH, null, RecipientType.USER,
                    NotificationChannel.PUSH, title, body, "topic:" + topic
            );
        } catch (Exception e) {
            log.error("Failed to send push notification to topic: {}", topic, e);
            notificationLogService.logFailure(
                    NotificationType.PUSH, null, RecipientType.USER,
                    NotificationChannel.PUSH, title, body, e.getMessage()
            );
        }
    }

    /**
     * Send push notification to a specific recipient with logging against a known recipient ID.
     */
    public void sendPushToRecipient(String fcmToken, String title, String body, UUID recipientId, RecipientType recipientType) {
        log.info("Sending push notification to recipient: {}", recipientId);
        try {
            String serviceAccountKeyPath = notificationProperties.getFcm().getServiceAccountKeyPath();
            if (serviceAccountKeyPath == null || serviceAccountKeyPath.isEmpty()) {
                log.warn("FCM service account key path not configured. Push notification not sent.");
                notificationLogService.logFailure(
                        NotificationType.PUSH, recipientId, recipientType,
                        NotificationChannel.PUSH, title, body,
                        "FCM service account key not configured"
                );
                return;
            }

            Map<String, Object> message = new HashMap<>();
            Map<String, Object> messageWrapper = new HashMap<>();
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", title);
            notification.put("body", body);

            messageWrapper.put("token", fcmToken);
            messageWrapper.put("notification", notification);
            message.put("message", messageWrapper);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(getAccessToken());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);

            String projectId = extractProjectId(serviceAccountKeyPath);
            String fcmUrl = String.format(FCM_API_URL, projectId);

            String response = restTemplate.postForObject(fcmUrl, request, String.class);
            log.info("Push notification sent successfully to recipient: {}. Response: {}", recipientId, response);

            notificationLogService.logSuccess(
                    NotificationType.PUSH, recipientId, recipientType,
                    NotificationChannel.PUSH, title, body, fcmToken
            );
        } catch (Exception e) {
            log.error("Failed to send push notification to recipient: {}", recipientId, e);
            notificationLogService.logFailure(
                    NotificationType.PUSH, recipientId, recipientType,
                    NotificationChannel.PUSH, title, body, e.getMessage()
            );
        }
    }

    /**
     * Extract project ID from service account key path.
     * In production, this would parse the service account JSON file.
     */
    private String extractProjectId(String serviceAccountKeyPath) {
        // In production, read and parse the service account JSON file
        // For now, return a placeholder project ID
        return "serenity-jms";
    }

    /**
     * Get OAuth2 access token for FCM API.
     * In production, this would use Google's service account authentication.
     */
    private String getAccessToken() {
        // In production, use GoogleCredentials from service account key file
        // For now, return a placeholder
        return "placeholder-fcm-access-token";
    }
}
