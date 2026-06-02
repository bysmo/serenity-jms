package com.bysmo.serenity.nanocredit.repository;

import com.bysmo.serenity.nanocredit.entity.NanoCredit;
import com.bysmo.serenity.nanocredit.entity.enums.NanoCreditStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NanoCreditRepository extends JpaRepository<NanoCredit, UUID> {

    List<NanoCredit> findByMembreId(UUID membreId);

    List<NanoCredit> findByStatut(NanoCreditStatut statut);

    List<NanoCredit> findByMembreIdAndStatut(UUID membreId, NanoCreditStatut statut);

    @Query("SELECT nc FROM NanoCredit nc WHERE nc.membreId = :membreId AND nc.statut NOT IN :excludedStatuts")
    List<NanoCredit> findByMembreIdAndStatutNotIn(@Param("membreId") UUID membreId, @Param("excludedStatuts") List<NanoCreditStatut> excludedStatuts);

    @Query("SELECT nc FROM NanoCredit nc WHERE nc.statut IN :statuts")
    List<NanoCredit> findByStatutIn(@Param("statuts") List<NanoCreditStatut> statuts);

    long countByMembreIdAndStatutIn(UUID membreId, List<NanoCreditStatut> statuts);

    boolean existsByMembreIdAndStatutNotIn(UUID membreId, List<NanoCreditStatut> excludedStatuts);
}
