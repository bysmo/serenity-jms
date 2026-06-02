package com.bysmo.serenity.member.repository;

import com.bysmo.serenity.member.entity.MembreWalletAlias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembreWalletAliasRepository extends JpaRepository<MembreWalletAlias, UUID> {

    List<MembreWalletAlias> findByMembreId(UUID membreId);

    Optional<MembreWalletAlias> findByAlias(String alias);
}
