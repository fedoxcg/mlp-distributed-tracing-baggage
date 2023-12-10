package com.fedox.study.tracing.checkout.services;

import com.fedox.study.tracing.checkout.resolvers.ApplicationPropertiesResolver;
import com.fedox.study.tracing.checkout.responsemodels.BillingPaymentResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

@Service
public class BillingService {

    private final RestTemplate restTemplate;

    private final ApplicationPropertiesResolver propsResolver;

    @Autowired
    public BillingService(RestTemplate restTemplate, ApplicationPropertiesResolver propsResolver) {
        this.restTemplate = restTemplate;
        this.propsResolver = propsResolver;
    }

    public Optional<BillingPaymentResponseModel> payment(HttpHeaders httpHeaders) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        return Optional.ofNullable(
                restTemplate.exchange(
                        propsResolver.getBillingServicePaymentEndpoint(),
                        HttpMethod.GET,
                        httpEntity,
                        BillingPaymentResponseModel.class
                ).getBody()
        );
    }
}
