package com.bysmo.serenity.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "paydunya")
public class PayDunyaProperties {

    private String baseUrl = "https://app.paydunya.com/api/v1";
    private String masterKey;
    private String privateKey;
    private String publicKey;
    private String token;
    private String mode = "test";
    private String ipnUrl;
}
