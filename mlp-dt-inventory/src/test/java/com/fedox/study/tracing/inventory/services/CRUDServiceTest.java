package com.fedox.study.tracing.inventory.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CRUDServiceTest {

    @Test
    @DisplayName("create order")
    void createOrder() {
        log.info("format a single create order error: STARTED");
        var crudService = new CRUDService();
        var createdOrder = crudService.createOrder();

        Assertions.assertNotNull(createdOrder);
        Assertions.assertTrue(createdOrder.isPresent());
        Assertions.assertFalse(createdOrder.orElse(null).orderId().toString().isEmpty());
        Assertions.assertNotNull(createdOrder.orElse(null).orderDate());
        log.info("format a single create order error: FINISHED");
    }
}