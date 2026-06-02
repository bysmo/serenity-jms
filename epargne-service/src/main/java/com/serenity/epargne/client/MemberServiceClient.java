package com.serenity.epargne.client;

import com.serenity.epargne.client.dto.ApiResponse;
import com.serenity.epargne.client.dto.MembreSummaryResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "member-service")
@CircuitBreaker(name = "memberService")
@Retry(name = "memberService")
public interface MemberServiceClient {

    @GetMapping("/api/v1/members/{id}")
    ApiResponse<MembreSummaryResponse> getMemberById(@PathVariable UUID id);
}
