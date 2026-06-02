package com.bysmo.serenity.payment.repository;

import com.bysmo.serenity.payment.entity.PaymentMethod;
import com.bysmo.serenity.payment.entity.enums.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {

    Optional<PaymentMethod> findByCode(String code);

    List<PaymentMethod> findByGateway(PaymentGateway gateway);

    List<PaymentMethod> findByIsActive(Boolean isActive);

    List<PaymentMethod> findByGatewayAndIsActive(PaymentGateway gateway, Boolean isActive);

    boolean existsByCode(String code);
}
