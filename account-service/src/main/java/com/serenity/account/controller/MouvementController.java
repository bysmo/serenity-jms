package com.serenity.account.controller;

import com.serenity.account.dto.MouvementCaisseResponse;
import com.serenity.account.entity.enums.MouvementType;
import com.serenity.account.entity.enums.Sens;
import com.serenity.account.mapper.MouvementCaisseMapper;
import com.serenity.account.repository.MouvementCaisseRepository;
import com.serenity.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
public class MouvementController {

    private final MouvementCaisseRepository mouvementCaisseRepository;
    private final MouvementCaisseMapper mouvementCaisseMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MouvementCaisseResponse>>> listMouvements(
            @RequestParam(required = false) UUID caisseId,
            @RequestParam(required = false) MouvementType type,
            @RequestParam(required = false) Sens sens,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        LocalDateTime debut = dateDebut != null ? dateDebut.atStartOfDay() : null;
        LocalDateTime fin = dateFin != null ? dateFin.atTime(LocalTime.MAX) : null;

        List<MouvementCaisseResponse> mouvements = mouvementCaisseMapper.toResponseList(
                mouvementCaisseRepository.findByFilters(caisseId, type, sens, debut, fin));

        return ResponseEntity.ok(ApiResponse.success(mouvements));
    }

    @GetMapping("/caisse/{caisseId}")
    public ResponseEntity<ApiResponse<List<MouvementCaisseResponse>>> listByCaisse(
            @PathVariable UUID caisseId) {
        List<MouvementCaisseResponse> mouvements = mouvementCaisseMapper.toResponseList(
                mouvementCaisseRepository.findByCaisseIdOrderByDateOperationDesc(caisseId));
        return ResponseEntity.ok(ApiResponse.success(mouvements));
    }
}
