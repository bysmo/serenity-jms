package com.serenity.collector.client;

import com.serenity.collector.client.dto.AccountingEntryRequest;
import com.serenity.collector.client.dto.CaisseBalanceResponse;
import com.serenity.collector.client.dto.MouvementCaisseResponse;
import com.serenity.common.dto.ApiResponse;
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

    @GetMapping("/api/v1/accounts/{id}/balance")
    ApiResponse<CaisseBalanceResponse> getCaisseBalance(@PathVariable("id") UUID id);
}
