package com.serenity.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a random 6-digit OTP code.
     *
     * @return a 6-digit OTP string
     */
    public String generateOtp() {
        int otpValue = secureRandom.nextInt(1_000_000);
        String otp = String.format("%0" + OTP_LENGTH + "d", otpValue);
        log.debug("Generated OTP code");
        return otp;
    }

    /**
     * Verifies that the provided OTP code matches the expected code.
     *
     * @param otpCode      the code provided by the user
     * @param expectedCode the expected OTP code stored in the collecte
     * @return true if codes match, false otherwise
     */
    public boolean verifyOtp(String otpCode, String expectedCode) {
        if (otpCode == null || expectedCode == null) {
            log.warn("OTP verification failed: null code provided");
            return false;
        }
        boolean matches = otpCode.equals(expectedCode);
        if (!matches) {
            log.warn("OTP verification failed: codes do not match");
        }
        return matches;
    }
}
