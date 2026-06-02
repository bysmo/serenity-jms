package com.serenity.member.service;

import com.serenity.member.dto.PinEnableRequest;
import com.serenity.member.dto.PinSetupRequest;
import com.serenity.member.entity.Membre;
import com.serenity.member.entity.enums.PinMode;
import com.serenity.member.exception.BusinessException;
import com.serenity.member.exception.InvalidPinException;
import com.serenity.member.exception.PinLockedException;
import com.serenity.member.exception.ResourceNotFoundException;
import com.serenity.member.repository.MembreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PinService {

    private final MembreRepository membreRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${pin.max-attempts:5}")
    private int maxAttempts;

    @Value("${pin.lockout-minutes:30}")
    private int lockoutMinutes;

    @Transactional
    public void setupPin(UUID membreId, PinSetupRequest request) {
        log.info("Setting up PIN for member: {}", membreId);

        Membre membre = findMembreOrThrow(membreId);

        String hashedPin = passwordEncoder.encode(request.getCodePin());
        membre.setCodePin(hashedPin);
        membre.setPinEnabled(true);
        membre.setPinAttempts(0);
        membre.setPinLockedUntil(null);
        membre.setPinMode(PinMode.EACH_TIME);

        membreRepository.save(membre);

        log.info("PIN setup successful for member: {}", membreId);
    }

    @Transactional
    public boolean verifyPin(UUID membreId, String codePin) {
        log.debug("Verifying PIN for member: {}", membreId);

        Membre membre = findMembreOrThrow(membreId);

        if (!membre.getPinEnabled()) {
            throw new BusinessException("PIN non activé pour ce membre");
        }

        if (membre.getCodePin() == null) {
            throw new BusinessException("Aucun PIN configuré pour ce membre");
        }

        // Check if PIN is locked
        if (membre.getPinLockedUntil() != null) {
            if (membre.getPinLockedUntil().isAfter(LocalDateTime.now())) {
                long remainingMinutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), membre.getPinLockedUntil()) + 1;
                log.warn("PIN locked for member: {} - {} minutes remaining", membreId, remainingMinutes);
                throw new PinLockedException(remainingMinutes);
            } else {
                // Lockout period has expired, reset
                log.info("PIN lockout expired for member: {}, resetting attempts", membreId);
                membre.setPinAttempts(0);
                membre.setPinLockedUntil(null);
            }
        }

        // Compare PIN
        boolean matches = passwordEncoder.matches(codePin, membre.getCodePin());

        if (matches) {
            // Correct PIN - reset attempts
            membre.setPinAttempts(0);
            membre.setPinLockedUntil(null);
            membreRepository.save(membre);
            log.info("PIN verified successfully for member: {}", membreId);
            return true;
        } else {
            // Wrong PIN - increment attempts
            int newAttempts = membre.getPinAttempts() + 1;
            membre.setPinAttempts(newAttempts);

            int attemptsRemaining = maxAttempts - newAttempts;

            if (newAttempts >= maxAttempts) {
                // Lock the PIN
                LocalDateTime lockoutUntil = LocalDateTime.now().plusMinutes(lockoutMinutes);
                membre.setPinLockedUntil(lockoutUntil);
                membreRepository.save(membre);
                log.warn("PIN locked for member: {} after {} failed attempts", membreId, newAttempts);
                throw new PinLockedException(lockoutMinutes);
            }

            membreRepository.save(membre);
            log.warn("Invalid PIN for member: {} - {} attempts remaining", membreId, attemptsRemaining);
            throw new InvalidPinException(attemptsRemaining);
        }
    }

    @Transactional
    public void enablePin(UUID membreId, boolean enable) {
        log.info("{} PIN for member: {}", enable ? "Enabling" : "Disabling", membreId);

        Membre membre = findMembreOrThrow(membreId);

        if (enable && membre.getCodePin() == null) {
            throw new BusinessException("Impossible d'activer le PIN : aucun PIN configuré. Veuillez d'abord configurer un PIN.");
        }

        membre.setPinEnabled(enable);
        membreRepository.save(membre);

        log.info("PIN {} for member: {}", enable ? "enabled" : "disabled", membreId);
    }

    @Transactional
    public void changePinMode(UUID membreId, PinMode mode) {
        log.info("Changing PIN mode to {} for member: {}", mode, membreId);

        Membre membre = findMembreOrThrow(membreId);

        if (!membre.getPinEnabled()) {
            throw new BusinessException("Impossible de changer le mode PIN : PIN non activé");
        }

        membre.setPinMode(mode);
        membreRepository.save(membre);

        log.info("PIN mode changed to {} for member: {}", mode, membreId);
    }

    @Transactional
    public void resetPinAttempts(UUID membreId) {
        log.info("Resetting PIN attempts for member: {}", membreId);

        Membre membre = findMembreOrThrow(membreId);
        membre.setPinAttempts(0);
        membre.setPinLockedUntil(null);
        membreRepository.save(membre);

        log.info("PIN attempts reset for member: {}", membreId);
    }

    private Membre findMembreOrThrow(UUID id) {
        return membreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membre", "id", id));
    }
}
