package com.bysmo.serenity.notification.repository;

import com.bysmo.serenity.notification.entity.SmsGateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SmsGatewayRepository extends JpaRepository<SmsGateway, UUID> {

    List<SmsGateway> findByIsActiveTrueOrderByOrdre();
}
