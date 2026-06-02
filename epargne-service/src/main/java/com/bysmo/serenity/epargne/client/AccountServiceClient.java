package com.bysmo.serenity.epargne.client;

import com.bysmo.serenity.epargne.client.dto.AccountingEntryRequest;
import com.bysmo.serenity.epargne.client.dto.ApiResponse;
import com.bysmo.serenity.epargne.client.dto.CaisseRequest;
import com.bysmo.serenity.epargne.client.dto.CaisseResponse;
import com.bysmo.serenity.epargne.client.dto.MouvementCaisseResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service")
@CircuitBreaker(name = "accountService")
@Retry(name = "accountService")
public interface AccountServiceClient {

    @PostMapping("/api/v1/movements")
    ApiResponse<MouvementCaisseResponse> recordMouvement(@RequestBody AccountingEntryRequest request);

    @PostMapping("/api/v1/accounts")
    ApiResponse<CaisseResponse> createCaisse(@RequestBody CaisseRequest request);
}
