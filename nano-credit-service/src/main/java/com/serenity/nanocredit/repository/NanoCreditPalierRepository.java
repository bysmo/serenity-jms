package com.serenity.nanocredit.repository;

import com.serenity.nanocredit.entity.NanoCreditPalier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NanoCreditPalierRepository extends JpaRepository<NanoCreditPalier, UUID> {

    Optional<NanoCreditPalier> findByNumero(String numero);

    List<NanoCreditPalier> findByActifTrue();

    boolean existsByNumero(String numero);
}
