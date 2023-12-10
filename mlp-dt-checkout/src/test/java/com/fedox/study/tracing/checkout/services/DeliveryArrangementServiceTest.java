package com.fedox.study.tracing.checkout.services;

import com.fedox.study.tracing.checkout.dto.DeliveryArrangement;
import com.fedox.study.tracing.checkout.dto.DeliveryArrangementStatus;
import com.fedox.study.tracing.checkout.resolvers.ApplicationPropertiesResolver;
import com.fedox.study.tracing.checkout.responsemodels.ArrangeDeliveryResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryArrangementServiceTest {

    private static final String DELIVERY_SERVICE_ARRANGE_ENDPOINT = "http://localhost:8084/delivery/arrange";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApplicationPropertiesResolver propsResolver;

    @InjectMocks
    private DeliveryArrangementService deliveryArrangementService;

    @Test
    @DisplayName("arrange a delivery")
    void arrangeDelivery() {
        log.info("arrange delivery from delivery service test: STARTED");

        final var deliveryArrangement = new DeliveryArrangement("SDA", DeliveryArrangementStatus.PREPARED);
        final var arrangeServiceResponse = new ArrangeDeliveryResponseModel(
                HttpStatus.OK.value(),
                Optional.of(deliveryArrangement),
                null);

        Mockito
                .when(propsResolver.getArrangeDeliveryServiceEndpoint())
                .thenReturn(DELIVERY_SERVICE_ARRANGE_ENDPOINT);
        Mockito
                .doReturn(new ResponseEntity<>(arrangeServiceResponse, HttpStatus.OK))
                .when(restTemplate)
                .exchange(
                        Mockito.eq(DELIVERY_SERVICE_ARRANGE_ENDPOINT),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        ArgumentMatchers.<Class<ArrangeDeliveryResponseModel>>any()
                );

        var deliveryResponse = deliveryArrangementService.arrangeDelivery(new HttpHeaders());

        Assertions.assertTrue(deliveryResponse.isPresent());
        Assertions.assertEquals(200, deliveryResponse.get().httpStatusCode());
        Assertions.assertEquals(deliveryArrangement, deliveryResponse.get().deliveryArrangement().orElse(null));
        Assertions.assertNull(deliveryResponse.get().errors());

        Mockito
                .verify(propsResolver, Mockito.times(1))
                .getArrangeDeliveryServiceEndpoint();
        Mockito
                .verify(restTemplate, Mockito.times(1))
                .exchange(
                    Mockito.eq(DELIVERY_SERVICE_ARRANGE_ENDPOINT),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.any(HttpEntity.class),
                    ArgumentMatchers.<Class<ArrangeDeliveryResponseModel>>any()
                );

        log.info("arrange delivery from delivery service test: FINISHED");
    }
}