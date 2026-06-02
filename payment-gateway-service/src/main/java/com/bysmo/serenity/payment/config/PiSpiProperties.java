package com.bysmo.serenity.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pispi")
public class PiSpiProperties {

    private String baseUrl = "https://api.pispi.com/v1";
    private String clientId;
    private String clientSecret;
    private String apiKey;
    private String payeAlias;
    private String mode = "test";
}
