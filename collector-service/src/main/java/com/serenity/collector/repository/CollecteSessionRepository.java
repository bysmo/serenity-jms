package com.serenity.collector.repository;

import com.serenity.collector.entity.CollecteSession;
import com.serenity.collector.entity.enums.SessionStatut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollecteSessionRepository extends JpaRepository<CollecteSession, UUID> {

    Optional<CollecteSession> findByUserIdAndStatut(UUID userId, SessionStatut statut);

    Page<CollecteSession> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    boolean existsByUserIdAndStatut(UUID userId, SessionStatut statut);
}
