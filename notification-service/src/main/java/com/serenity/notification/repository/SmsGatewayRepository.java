package com.serenity.notification.repository;

import com.serenity.notification.entity.SmsGateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SmsGatewayRepository extends JpaRepository<SmsGateway, UUID> {

    List<SmsGateway> findByIsActiveTrueOrderByOrdre();
}
