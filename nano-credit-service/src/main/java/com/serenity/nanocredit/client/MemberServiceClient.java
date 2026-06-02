package com.serenity.nanocredit.client;

import com.serenity.common.dto.ApiResponse;
import com.serenity.nanocredit.client.dto.MembreSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "member-service")
public interface MemberServiceClient {

    @GetMapping("/api/v1/members/{id}")
    ApiResponse<MembreSummaryResponse> getMemberById(@PathVariable UUID id);
}
