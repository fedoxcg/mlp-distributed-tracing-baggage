package com.fedox.study.tracing.delivery.services;

import com.fedox.study.tracing.delivery.resolvers.ApplicationPropertiesResolver;
import com.fedox.study.tracing.delivery.responsemodels.LogisticTransportResponseModel;
import com.fedox.study.tracing.delivery.responsemodels.LogisticTransportStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class EShopLogisticServiceTest {

    public static final String LOGISTIC_TRANSPORT_ENDPOINT = "http://eshop-logistic:8085/transport";

    @Mock
    private ApplicationPropertiesResolver propsResolver;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EShopLogisticService logisticService;

    @Test
    void transport() {
        ResponseEntity<LogisticTransportResponseModel> transportResponse = new ResponseEntity<>(new LogisticTransportResponseModel(
                HttpStatus.OK.value(),
                LogisticTransportStatus.PREPARATION,
                null
        ), HttpStatus.CREATED);

        Mockito
                .when(propsResolver.getArrangeDeliveryServiceEndpoint())
                .thenReturn(LOGISTIC_TRANSPORT_ENDPOINT);
        Mockito
                .doReturn(transportResponse)
                .when(restTemplate)
                .exchange(
                        Mockito.eq(LOGISTIC_TRANSPORT_ENDPOINT),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        ArgumentMatchers.<Class<LogisticTransportResponseModel>>any()
                );

        var logisticResponse = logisticService.transport(new HttpHeaders());

        Assertions.assertTrue(logisticResponse.isPresent());
        Assertions.assertEquals(
                HttpStatus.OK.value(),
                logisticResponse.orElse(null).httpStatus()
        );
        Assertions.assertEquals(
                LogisticTransportStatus.PREPARATION,
                logisticResponse.orElse(null).status()
        );
        Assertions.assertNull(logisticResponse.orElse(null).errors());

        Mockito
                .verify(propsResolver, Mockito.times(1))
                .getArrangeDeliveryServiceEndpoint();
        Mockito
                .verify(restTemplate, Mockito.times(1))
                .exchange(
                        Mockito.eq(LOGISTIC_TRANSPORT_ENDPOINT),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        ArgumentMatchers.<Class<LogisticTransportResponseModel>>any()
                );
    }
}