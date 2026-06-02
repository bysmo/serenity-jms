package com.bysmo.serenity.member.mapper;

import com.bysmo.serenity.member.dto.WalletAliasRequest;
import com.bysmo.serenity.member.dto.WalletAliasResponse;
import com.bysmo.serenity.member.entity.MembreWalletAlias;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletAliasMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "membreId", ignore = true)
    @Mapping(target = "actif", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MembreWalletAlias toEntity(WalletAliasRequest request);

    WalletAliasResponse toResponse(MembreWalletAlias alias);

    List<WalletAliasResponse> toResponseList(List<MembreWalletAlias> aliases);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "membreId", ignore = true)
    @Mapping(target = "actif", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(WalletAliasRequest request, @MappingTarget MembreWalletAlias alias);
}
