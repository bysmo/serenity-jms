package com.bysmo.serenity.notification.config;

import com.bysmo.serenity.notification.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final OtpService otpService;

    /**
     * Clean up expired OTPs every 5 minutes.
     */
    @Scheduled(fixedRate = 300000)
    public void cleanupExpiredOtps() {
        log.debug("Running scheduled OTP cleanup...");
        otpService.cleanupExpiredOtps();
    }
}
