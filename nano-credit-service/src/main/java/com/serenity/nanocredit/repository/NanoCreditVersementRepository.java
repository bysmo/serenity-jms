package com.serenity.nanocredit.repository;

import com.serenity.nanocredit.entity.NanoCreditVersement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NanoCreditVersementRepository extends JpaRepository<NanoCreditVersement, UUID> {

    List<NanoCreditVersement> findByNanoCreditIdOrderByDateVersementDesc(UUID nanoCreditId);

    List<NanoCreditVersement> findByEcheanceId(UUID echeanceId);
}
