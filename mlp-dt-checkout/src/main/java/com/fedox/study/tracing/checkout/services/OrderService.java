package com.fedox.study.tracing.checkout.services;

import com.fedox.study.tracing.checkout.resolvers.ApplicationPropertiesResolver;
import com.fedox.study.tracing.checkout.responsemodels.InventoryOrderResponseModel;
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
public class OrderService {

    private final RestTemplate restTemplate;
    private final ApplicationPropertiesResolver propsResolver;

    @Autowired
    public OrderService(RestTemplate restTemplate, ApplicationPropertiesResolver propertiesResolver) {
        this.restTemplate = restTemplate;
        this.propsResolver = propertiesResolver;
    }

    public Optional<InventoryOrderResponseModel> createOrder(HttpHeaders httpHeaders) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        return Optional.ofNullable(
                restTemplate.exchange(
                        propsResolver.getOrderServiceEndpoint(),
                        HttpMethod.GET,
                        httpEntity,
                        InventoryOrderResponseModel.class
                ).getBody()
        );
    }

}
