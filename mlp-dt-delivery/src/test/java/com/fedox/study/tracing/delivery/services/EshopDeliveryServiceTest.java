package com.fedox.study.tracing.delivery.services;

import com.fedox.study.tracing.delivery.dto.DeliveryArrangementStatus;
import com.fedox.study.tracing.delivery.responsemodels.LogisticTransportResponseModel;
import com.fedox.study.tracing.delivery.responsemodels.LogisticTransportStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EshopDeliveryServiceTest {

    @Mock
    private EShopLogisticService logisticService;

    @InjectMocks
    private EshopDeliveryService deliveryService;

    @Test
    void arrangeDelivery() {
        var transportResponse = new LogisticTransportResponseModel(
                HttpStatus.OK.value(),
                LogisticTransportStatus.PREPARATION,
                null
        );

        Mockito
                .when(logisticService.transport(new HttpHeaders()))
                .thenReturn(Optional.of(transportResponse));

        var deliveryResponse = deliveryService.arrangeDelivery(new HttpHeaders());

        var deliveryArrangement = deliveryResponse.orElse(null).deliveryArrangement().orElse(null);
        Assertions.assertNotNull(deliveryResponse);
        Assertions.assertTrue(deliveryResponse.isPresent());
        Assertions.assertEquals(HttpStatus.CREATED.value(), deliveryResponse.orElse(null).httpStatusCode());
        Assertions.assertNotNull(deliveryArrangement);
        Assertions.assertEquals("SDA", deliveryArrangement.courier());
        Assertions.assertEquals(DeliveryArrangementStatus.PREPARED, deliveryArrangement.status());
        Assertions.assertNull(deliveryResponse.orElse(null).errors());

        Mockito
                .verify(logisticService, Mockito.times(1))
                .transport(new HttpHeaders());
    }
}