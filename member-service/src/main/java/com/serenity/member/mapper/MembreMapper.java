package com.serenity.member.mapper;

import com.serenity.member.dto.MembreRequest;
import com.serenity.member.dto.MembreResponse;
import com.serenity.member.entity.Membre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MembreMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numero", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "codePin", ignore = true)
    @Mapping(target = "pinEnabled", ignore = true)
    @Mapping(target = "pinAttempts", ignore = true)
    @Mapping(target = "pinLockedUntil", ignore = true)
    @Mapping(target = "pinMode", ignore = true)
    @Mapping(target = "nanoCreditEligible", ignore = true)
    @Mapping(target = "nanoCreditLimite", ignore = true)
    @Mapping(target = "nanoCreditSolde", ignore = true)
    @Mapping(target = "parrainId", ignore = true)
    @Mapping(target = "codeParrainage", ignore = true)
    @Mapping(target = "parrainageActif", ignore = true)
    @Mapping(target = "niveauParrainage", ignore = true)
    @Mapping(target = "emailVerifie", ignore = true)
    @Mapping(target = "telephoneVerifie", ignore = true)
    @Mapping(target = "kycNiveau", ignore = true)
    @Mapping(target = "pushToken", ignore = true)
    @Mapping(target = "pushEnabled", ignore = true)
    @Mapping(target = "checksum", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Membre toEntity(MembreRequest request);

    MembreResponse toResponse(Membre membre);

    List<MembreResponse> toResponseList(List<Membre> membres);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numero", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "codePin", ignore = true)
    @Mapping(target = "pinEnabled", ignore = true)
    @Mapping(target = "pinAttempts", ignore = true)
    @Mapping(target = "pinLockedUntil", ignore = true)
    @Mapping(target = "pinMode", ignore = true)
    @Mapping(target = "nanoCreditEligible", ignore = true)
    @Mapping(target = "nanoCreditLimite", ignore = true)
    @Mapping(target = "nanoCreditSolde", ignore = true)
    @Mapping(target = "parrainId", ignore = true)
    @Mapping(target = "codeParrainage", ignore = true)
    @Mapping(target = "parrainageActif", ignore = true)
    @Mapping(target = "niveauParrainage", ignore = true)
    @Mapping(target = "emailVerifie", ignore = true)
    @Mapping(target = "telephoneVerifie", ignore = true)
    @Mapping(target = "kycNiveau", ignore = true)
    @Mapping(target = "pushToken", ignore = true)
    @Mapping(target = "pushEnabled", ignore = true)
    @Mapping(target = "checksum", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(MembreRequest request, @MappingTarget Membre membre);
}
