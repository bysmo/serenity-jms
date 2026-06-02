package com.bysmo.serenity.cotisation.client;

import com.bysmo.serenity.cotisation.client.dto.MembreSummaryResponse;
import com.bysmo.serenity.cotisation.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "member-service")
public interface MemberServiceClient {

    @GetMapping("/api/v1/members/{id}")
    ApiResponse<MembreSummaryResponse> getMemberById(@PathVariable("id") UUID id);

    @GetMapping("/api/v1/members/search")
    ApiResponse<MembreSummaryResponse> searchByTelephone(@RequestParam("telephone") String telephone);
}
