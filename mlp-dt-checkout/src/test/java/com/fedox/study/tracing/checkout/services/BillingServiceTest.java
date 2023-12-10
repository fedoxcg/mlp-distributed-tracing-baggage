package com.fedox.study.tracing.checkout.services;

import com.fedox.study.tracing.checkout.dto.PaymentReceipt;
import com.fedox.study.tracing.checkout.dto.PaymentStatus;
import com.fedox.study.tracing.checkout.resolvers.ApplicationPropertiesResolver;
import com.fedox.study.tracing.checkout.responsemodels.BillingPaymentResponseModel;
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

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    private static final String BILLING_PAYMENT_SERVICE_ENDPOINT = "http://localhost:8083/payment";
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ApplicationPropertiesResolver propsResolver;

    @InjectMocks
    private BillingService paymentService;

    @Test
    @DisplayName("perform a payment during checkout")
    void checkoutPaymentTest() {
        log.info("pay from payment service test: STARTED");

        PaymentReceipt paymentReceipt = new PaymentReceipt(BigDecimal.TEN, LocalDate.now(), PaymentStatus.OK);
        BillingPaymentResponseModel paymentResponseModel = new BillingPaymentResponseModel(
                HttpStatus.OK.value(), Optional.of(paymentReceipt), null);

        Mockito
                .when(propsResolver.getBillingServicePaymentEndpoint())
                .thenReturn(BILLING_PAYMENT_SERVICE_ENDPOINT);
        Mockito
                .doReturn(new ResponseEntity<>(paymentResponseModel, HttpStatus.OK))
                .when(restTemplate)
                .exchange(
                        Mockito.eq(BILLING_PAYMENT_SERVICE_ENDPOINT),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        ArgumentMatchers.<Class<BillingPaymentResponseModel>>any()
                );

        Optional<BillingPaymentResponseModel> paymentResponse = paymentService.payment(new HttpHeaders());

        Assertions.assertTrue(paymentResponse.isPresent());
        Assertions.assertEquals(200, paymentResponse.get().httpStatusCode());
        Assertions.assertEquals(paymentReceipt, paymentResponse.get().paymentReceipt().orElse(null));
        Assertions.assertNull(paymentResponse.get().errors());

        Mockito
                .verify(propsResolver, Mockito.times(1))
                .getBillingServicePaymentEndpoint();
        Mockito
                .verify(restTemplate, Mockito.times(1))
                .exchange(
                        Mockito.eq(BILLING_PAYMENT_SERVICE_ENDPOINT),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        ArgumentMatchers.<Class<BillingPaymentResponseModel>>any()
                );

        log.info("pay from payment service test: FINISHED");
    }
}