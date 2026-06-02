package com.serenity.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {

    private SmsConfig sms = new SmsConfig();
    private FcmConfig fcm = new FcmConfig();
    private List<String> adminEmails = List.of();

    @Getter
    @Setter
    public static class SmsConfig {
        private String defaultProvider = "orange";
        private Map<String, SmsProviderConfig> providers = Map.of();
    }

    @Getter
    @Setter
    public static class SmsProviderConfig {
        private String apiUrl;
        private String apiKey;
        private String senderName;
    }

    @Getter
    @Setter
    public static class FcmConfig {
        private String serviceAccountKeyPath;
    }
}
