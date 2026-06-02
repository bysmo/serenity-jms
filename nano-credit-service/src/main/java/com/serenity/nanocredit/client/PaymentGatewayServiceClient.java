package com.serenity.nanocredit.client;

import com.serenity.common.dto.ApiResponse;
import com.serenity.nanocredit.client.dto.DisbursementRequest;
import com.serenity.nanocredit.client.dto.PaymentTransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-gateway-service")
public interface PaymentGatewayServiceClient {

    @PostMapping("/api/v1/payment-gateways/transactions/disburse")
    ApiResponse<PaymentTransactionResponse> disburse(@RequestBody DisbursementRequest request);
}
