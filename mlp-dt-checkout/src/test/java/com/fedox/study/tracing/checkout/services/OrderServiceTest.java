package com.fedox.study.tracing.checkout.services;

import com.fedox.study.tracing.checkout.dto.Order;
import com.fedox.study.tracing.checkout.resolvers.ApplicationPropertiesResolver;
import com.fedox.study.tracing.checkout.responsemodels.InventoryOrderResponseModel;
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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ExtendWith({MockitoExtension.class})
class OrderServiceTest {

    private static final String ORDER_SERVICE_ENDPOINT = "http://localhost:8082/createOrder";
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ApplicationPropertiesResolver propsResolver;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("get order from createOrder service")
    void createOrder() {
        log.info("get order from createOrder service test: STARTED");

        Order order = new Order(UUID.randomUUID(), LocalDateTime.MIN);
        InventoryOrderResponseModel inventoryOrderResponseModel = new InventoryOrderResponseModel(201, Optional.of(order), null);

        Mockito
                .when(propsResolver.getOrderServiceEndpoint())
                .thenReturn(ORDER_SERVICE_ENDPOINT);
        Mockito
                .doReturn(new ResponseEntity<>(inventoryOrderResponseModel, HttpStatus.CREATED))
                .when(restTemplate)
                .exchange(
                        Mockito.eq(ORDER_SERVICE_ENDPOINT),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        ArgumentMatchers.<Class<InventoryOrderResponseModel>>any()
                );

        Optional<InventoryOrderResponseModel> orderResponse = orderService.createOrder(new HttpHeaders());

        Assertions.assertTrue(orderResponse.isPresent());
        Assertions.assertEquals(201, orderResponse.get().httpStatusCode());
        Assertions.assertEquals(order, orderResponse.get().order().get());
        Assertions.assertNull(orderResponse.get().errors());

        Mockito
                .verify(propsResolver, Mockito.times(1))
                .getOrderServiceEndpoint();
        Mockito
                .verify(restTemplate, Mockito.times(1))
                .exchange(
                        Mockito.eq(ORDER_SERVICE_ENDPOINT),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        ArgumentMatchers.<Class<InventoryOrderResponseModel>>any()
                );

        log.info("get order from createOrder service test: FINISHED");
    }

    @Test
    @DisplayName("create order with no 201 response")
    void createOrderNot201Response() {
        log.info("create order with no 201 response service test: STARTED");

        final var ERROR_DESC = "Internal Server Error";
        InventoryOrderResponseModel inventoryOrderResponseModel = new InventoryOrderResponseModel(500, Optional.empty(), Map.of(ERROR_DESC, ERROR_DESC));

        Mockito
                .when(propsResolver.getOrderServiceEndpoint())
                .thenReturn(ORDER_SERVICE_ENDPOINT);
        Mockito
                .doReturn(new ResponseEntity<>(inventoryOrderResponseModel, HttpStatus.CREATED))
                .when(restTemplate)
                .exchange(
                        Mockito.eq(ORDER_SERVICE_ENDPOINT),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        ArgumentMatchers.<Class<InventoryOrderResponseModel>>any()
                );

        Optional<InventoryOrderResponseModel> orderResponse = orderService.createOrder(new HttpHeaders());

        Assertions.assertTrue(orderResponse.isPresent());
        Assertions.assertFalse(orderResponse.get().order().isPresent());
        Assertions.assertNotEquals(201, orderResponse.get().httpStatusCode());
        Assertions.assertNotNull(orderResponse.get().errors());
        Assertions.assertEquals(1, orderResponse.get().errors().size());
        Assertions.assertTrue(orderResponse.get().errors().containsKey(ERROR_DESC));
        Assertions.assertEquals(ERROR_DESC, orderResponse.get().errors().get(ERROR_DESC));

        Mockito
                .verify(propsResolver, Mockito.times(1))
                .getOrderServiceEndpoint();
        Mockito
                .verify(restTemplate, Mockito.times(1))
                .exchange(
                        Mockito.eq(ORDER_SERVICE_ENDPOINT),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        ArgumentMatchers.<Class<InventoryOrderResponseModel>>any()
                );

        log.info("create order with no 201 response test: FINISHED");
    }
}