package com.bysmo.serenity.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_MINUTES = 5;

    private final ConcurrentHashMap<String, OtpEntry> otpCache = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generate a 6-digit OTP and store it with a 5-minute expiry.
     *
     * @param telephone the phone number to associate the OTP with
     * @return the generated OTP code
     */
    public String generateOtp(String telephone) {
        String otp = generateRandomOtp();
        otpCache.put(telephone, new OtpEntry(otp, Instant.now().plusSeconds(OTP_EXPIRY_MINUTES * 60)));
        log.info("OTP generated for telephone: {}****", telephone != null && telephone.length() > 4
                ? telephone.substring(telephone.length() - 4) : telephone);
        return otp;
    }

    /**
     * Verify an OTP code against the stored value for the given telephone number.
     *
     * @param telephone the phone number
     * @param code      the OTP code to verify
     * @return true if the OTP is valid and not expired, false otherwise
     */
    public boolean verifyOtp(String telephone, String code) {
        OtpEntry entry = otpCache.get(telephone);

        if (entry == null) {
            log.warn("No OTP found for telephone: {}****", telephone != null && telephone.length() > 4
                    ? telephone.substring(telephone.length() - 4) : telephone);
            return false;
        }

        if (Instant.now().isAfter(entry.expiryTime())) {
            otpCache.remove(telephone);
            log.warn("OTP expired for telephone: {}****", telephone != null && telephone.length() > 4
                    ? telephone.substring(telephone.length() - 4) : telephone);
            return false;
        }

        if (entry.code().equals(code)) {
            otpCache.remove(telephone);
            log.info("OTP verified successfully for telephone: {}****", telephone != null && telephone.length() > 4
                    ? telephone.substring(telephone.length() - 4) : telephone);
            return true;
        }

        log.warn("Invalid OTP for telephone: {}****", telephone != null && telephone.length() > 4
                ? telephone.substring(telephone.length() - 4) : telephone);
        return false;
    }

    /**
     * Remove expired OTPs from the cache. Called by scheduled task.
     */
    public void cleanupExpiredOtps() {
        Instant now = Instant.now();
        otpCache.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiryTime()));
        log.debug("OTP cache cleanup completed. Current size: {}", otpCache.size());
    }

    /**
     * Generate a random 6-digit OTP.
     */
    private String generateRandomOtp() {
        int otp = secureRandom.nextInt((int) Math.pow(10, OTP_LENGTH));
        return String.format("%0" + OTP_LENGTH + "d", otp);
    }

    /**
     * Internal record to store OTP with expiry time.
     */
    private record OtpEntry(String code, Instant expiryTime) {
    }
}
