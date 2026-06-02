package com.serenity.member.service;

import com.serenity.member.dto.MembreDashboardResponse;
import com.serenity.member.dto.MembreRegistrationRequest;
import com.serenity.member.dto.MembreRequest;
import com.serenity.member.dto.MembreResponse;
import com.serenity.member.entity.Membre;
import com.serenity.member.entity.Segment;
import com.serenity.member.entity.enums.MembreStatut;
import com.serenity.member.event.MemberEventPublisher;
import com.serenity.member.exception.DuplicateResourceException;
import com.serenity.member.exception.ResourceNotFoundException;
import com.serenity.member.mapper.MembreMapper;
import com.serenity.member.repository.MembreRepository;
import com.serenity.member.repository.ParrainageCommissionRepository;
import com.serenity.member.repository.SegmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembreService {

    private final MembreRepository membreRepository;
    private final SegmentRepository segmentRepository;
    private final ParrainageCommissionRepository parrainageCommissionRepository;
    private final MembreMapper membreMapper;
    private final MemberEventPublisher memberEventPublisher;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<MembreResponse> getAll(Pageable pageable) {
        log.debug("Fetching all members with pagination");
        return membreRepository.findAll(pageable)
                .map(membreMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MembreResponse getById(UUID id) {
        log.debug("Fetching member by id: {}", id);
        Membre membre = findMembreOrThrow(id);
        return membreMapper.toResponse(membre);
    }

    @Transactional(readOnly = true)
    public MembreResponse getByNumero(String numero) {
        log.debug("Fetching member by numero: {}", numero);
        Membre membre = membreRepository.findByNumero(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Membre", "numero", numero));
        return membreMapper.toResponse(membre);
    }

    @Transactional(readOnly = true)
    public MembreResponse getByTelephone(String telephone) {
        log.debug("Fetching member by telephone: {}", telephone);
        Membre membre = membreRepository.findByTelephone(telephone)
                .orElseThrow(() -> new ResourceNotFoundException("Membre", "telephone", telephone));
        return membreMapper.toResponse(membre);
    }

    @Transactional
    public MembreResponse create(MembreRequest request) {
        log.info("Creating new member: {} {}", request.getPrenom(), request.getNom());

        validateUniqueness(request.getTelephone(), request.getEmail(), null);

        Membre membre = membreMapper.toEntity(request);

        UUID segmentId = request.getSegmentId();
        if (segmentId == null) {
            Segment defaultSegment = segmentRepository.findByIsDefaultTrue()
                    .orElseThrow(() -> new ResourceNotFoundException("Segment par défaut non trouvé"));
            segmentId = defaultSegment.getId();
        }
        membre.setSegmentId(segmentId);

        membre.setNumero(generateNumero());
        membre.setStatut(MembreStatut.EN_ATTENTE);
        membre.setChecksum(computeChecksum(membre));

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            membre.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        membre = membreRepository.save(membre);

        memberEventPublisher.publishMemberCreated(membre);

        log.info("Member created with numero: {} and id: {}", membre.getNumero(), membre.getId());
        return membreMapper.toResponse(membre);
    }

    @Transactional
    public MembreResponse register(MembreRegistrationRequest request) {
        log.info("Mobile registration for: {} {}", request.getPrenom(), request.getNom());

        String normalizedPhone = normalizePhoneNumber(request.getTelephone());

        membreRepository.findByTelephone(normalizedPhone).ifPresent(m -> {
            throw new DuplicateResourceException("Membre", "telephone", normalizedPhone);
        });

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            membreRepository.findByEmail(request.getEmail()).ifPresent(m -> {
                throw new DuplicateResourceException("Membre", "email", request.getEmail());
            });
        }

        Segment defaultSegment = segmentRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Segment par défaut non trouvé"));

        Membre membre = Membre.builder()
                .numero(generateNumero())
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .telephone(normalizedPhone)
                .password(passwordEncoder.encode(request.getPassword()))
                .statut(MembreStatut.EN_ATTENTE)
                .segmentId(defaultSegment.getId())
                .pays("SN")
                .pinEnabled(false)
                .pinAttempts(0)
                .pinMode(com.serenity.member.entity.enums.PinMode.EACH_TIME)
                .nanoCreditEligible(false)
                .nanoCreditLimite(BigDecimal.ZERO)
                .nanoCreditSolde(BigDecimal.ZERO)
                .parrainageActif(false)
                .niveauParrainage(0)
                .emailVerifie(false)
                .telephoneVerifie(false)
                .kycNiveau("NONE")
                .pushEnabled(true)
                .build();

        membre.setChecksum(computeChecksum(membre));

        membre = membreRepository.save(membre);

        memberEventPublisher.publishMemberCreated(membre);

        log.info("Member registered with numero: {} and id: {}", membre.getNumero(), membre.getId());
        return membreMapper.toResponse(membre);
    }

    @Transactional
    public MembreResponse update(UUID id, MembreRequest request) {
        log.info("Updating member: {}", id);

        Membre membre = findMembreOrThrow(id);

        validateUniqueness(request.getTelephone(), request.getEmail(), id);

        membreMapper.updateEntityFromRequest(request, membre);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            membre.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getSegmentId() != null) {
            membre.setSegmentId(request.getSegmentId());
        }

        membre.setChecksum(computeChecksum(membre));
        membre = membreRepository.save(membre);

        log.info("Member updated: {}", id);
        return membreMapper.toResponse(membre);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deleting member: {}", id);
        Membre membre = findMembreOrThrow(id);
        membreRepository.delete(membre);
        log.info("Member deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public MembreDashboardResponse updateDashboard(UUID id) {
        log.debug("Fetching dashboard for member: {}", id);

        Membre membre = findMembreOrThrow(id);

        Segment segment = segmentRepository.findById(membre.getSegmentId())
                .orElse(null);

        List<Membre> filleuls = membreRepository.findByParrainId(id);
        int nombreFilleuls = filleuls.size();

        BigDecimal totalCommissions = parrainageCommissionRepository.findByParrainId(id).stream()
                .filter(c -> c.getStatut() == com.serenity.member.entity.enums.CommissionStatut.DISPONIBLE
                        || c.getStatut() == com.serenity.member.entity.enums.CommissionStatut.EN_ATTENTE)
                .map(com.serenity.member.entity.ParrainageCommission::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return MembreDashboardResponse.builder()
                .id(membre.getId())
                .numero(membre.getNumero())
                .nom(membre.getNom())
                .prenom(membre.getPrenom())
                .email(membre.getEmail())
                .telephone(membre.getTelephone())
                .statut(membre.getStatut())
                .segmentNom(segment != null ? segment.getNom() : null)
                .segmentCouleur(segment != null ? segment.getCouleur() : null)
                .segmentIcone(segment != null ? segment.getIcone() : null)
                .nanoCreditEligible(membre.getNanoCreditEligible())
                .nanoCreditLimite(membre.getNanoCreditLimite())
                .nanoCreditSolde(membre.getNanoCreditSolde())
                .kycNiveau(membre.getKycNiveau())
                .emailVerifie(membre.getEmailVerifie())
                .telephoneVerifie(membre.getTelephoneVerifie())
                .pinEnabled(membre.getPinEnabled())
                .codeParrainage(membre.getCodeParrainage())
                .parrainageActif(membre.getParrainageActif())
                .nombreFilleuls(nombreFilleuls)
                .totalCommissions(totalCommissions)
                .build();
    }

    private Membre findMembreOrThrow(UUID id) {
        return membreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membre", "id", id));
    }

    private String generateNumero() {
        Membre lastMembre = membreRepository.findTopByOrderByNumeroDesc().orElse(null);
        int nextNumber = 1;

        if (lastMembre != null && lastMembre.getNumero() != null) {
            try {
                String numStr = lastMembre.getNumero().replace("MBR-", "");
                nextNumber = Integer.parseInt(numStr) + 1;
            } catch (NumberFormatException e) {
                log.warn("Could not parse last member number: {}, starting from 1", lastMembre.getNumero());
                nextNumber = (int) membreRepository.count() + 1;
            }
        }

        return String.format("MBR-%06d", nextNumber);
    }

    private String computeChecksum(Membre membre) {
        String data = membre.getNumero() + membre.getNom() + membre.getPrenom()
                + membre.getTelephone() + membre.getStatut().name();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not found", e);
            return null;
        }
    }

    private String normalizePhoneNumber(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            return telephone;
        }
        String normalized = telephone.replaceAll("[\\s\\-\\.\\(\\)]", "");
        if (normalized.startsWith("+")) {
            return normalized;
        }
        if (normalized.startsWith("00")) {
            return "+" + normalized.substring(2);
        }
        if (normalized.startsWith("0")) {
            return "+221" + normalized.substring(1);
        }
        return "+" + normalized;
    }

    private void validateUniqueness(String telephone, String email, UUID excludeId) {
        membreRepository.findByTelephone(telephone).ifPresent(m -> {
            if (excludeId == null || !m.getId().equals(excludeId)) {
                throw new DuplicateResourceException("Membre", "telephone", telephone);
            }
        });

        if (email != null && !email.isBlank()) {
            membreRepository.findByEmail(email).ifPresent(m -> {
                if (excludeId == null || !m.getId().equals(excludeId)) {
                    throw new DuplicateResourceException("Membre", "email", email);
                }
            });
        }
    }
}
