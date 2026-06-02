package com.bysmo.serenity.nanocredit.client;

import com.bysmo.serenity.common.dto.ApiResponse;
import com.bysmo.serenity.nanocredit.client.dto.AccountingEntryRequest;
import com.bysmo.serenity.nanocredit.client.dto.CaisseBalanceResponse;
import com.bysmo.serenity.nanocredit.client.dto.CaisseRequest;
import com.bysmo.serenity.nanocredit.client.dto.CaisseResponse;
import com.bysmo.serenity.nanocredit.client.dto.MouvementCaisseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

    @PostMapping("/api/v1/movements")
    ApiResponse<MouvementCaisseResponse> recordMouvement(@RequestBody AccountingEntryRequest request);

    @PostMapping("/api/v1/accounts")
    ApiResponse<CaisseResponse> createCaisse(@RequestBody CaisseRequest request);

    @GetMapping("/api/v1/accounts/{id}/balance")
    ApiResponse<CaisseBalanceResponse> getCaisseBalance(@PathVariable("id") UUID id);
}
