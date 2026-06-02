package com.serenity.cotisation.client;

import com.serenity.cotisation.client.dto.AccountingEntryRequest;
import com.serenity.cotisation.client.dto.CaisseBalanceResponse;
import com.serenity.cotisation.client.dto.MouvementCaisseResponse;
import com.serenity.cotisation.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

    @GetMapping("/api/v1/accounts/{id}/balance")
    ApiResponse<CaisseBalanceResponse> getCaisseBalance(@PathVariable("id") UUID id);

    @PostMapping("/api/v1/movements")
    ApiResponse<MouvementCaisseResponse> recordMouvement(@RequestBody AccountingEntryRequest request);
}
