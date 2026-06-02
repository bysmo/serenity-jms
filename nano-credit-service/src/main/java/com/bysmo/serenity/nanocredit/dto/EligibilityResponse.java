package com.bysmo.serenity.nanocredit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityResponse {

    private boolean eligible;
    @Builder.Default
    private List<String> motifs = new ArrayList<>();

    public void addMotif(String motif) {
        motifs.add(motif);
    }
}
