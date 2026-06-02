package com.serenity.collector.client;

import com.serenity.collector.client.dto.MembreSummaryResponse;
import com.serenity.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "member-service")
public interface MemberServiceClient {

    @GetMapping("/api/v1/members/search")
    ApiResponse<MembreSummaryResponse> searchByTelephone(@RequestParam("telephone") String telephone);
}
