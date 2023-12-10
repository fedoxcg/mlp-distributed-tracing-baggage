package com.fedox.study.tracing.inventory.services;

import com.fedox.study.tracing.inventory.dto.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CRUDService {

    public Optional<Order> createOrder() {
        return Optional.of(new Order(UUID.randomUUID(), LocalDateTime.now()));
    }

}
