package com.fedox.study.tracing.billing.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CRUDServiceTest {

    @Test
    @DisplayName("perform payment")
    void pay() {
        log.info("format a single create order error: STARTED");
        var crudService = new CRUDService();
        var paymentReceipt = crudService.pay();

        Assertions.assertNotNull(paymentReceipt);
        Assertions.assertTrue(paymentReceipt.isPresent());
        Assertions.assertNotNull(paymentReceipt.orElse(null).status());
        Assertions.assertNotNull(paymentReceipt.orElse(null).amount());
        Assertions.assertNotNull(paymentReceipt.orElse(null).date());
        log.info("format a single create order error: FINISHED");
    }
}