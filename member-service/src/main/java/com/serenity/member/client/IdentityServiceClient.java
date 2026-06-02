package com.serenity.member.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "identity-service")
public interface IdentityServiceClient {

    @PostMapping("/api/v1/auth/register")
    Map<String, Object> registerUser(@RequestBody Map<String, Object> registrationRequest);
}
