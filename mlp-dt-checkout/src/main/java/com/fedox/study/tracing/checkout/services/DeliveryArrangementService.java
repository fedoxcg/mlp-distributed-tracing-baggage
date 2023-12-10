package com.fedox.study.tracing.checkout.services;

import com.fedox.study.tracing.checkout.resolvers.ApplicationPropertiesResolver;
import com.fedox.study.tracing.checkout.responsemodels.ArrangeDeliveryResponseModel;
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
public class DeliveryArrangementService {

    private final RestTemplate restTemplate;

    private final ApplicationPropertiesResolver propsResolver;

    @Autowired
    public DeliveryArrangementService(RestTemplate restTemplate, ApplicationPropertiesResolver propsResolver) {
        this.restTemplate = restTemplate;
        this.propsResolver = propsResolver;
    }

    public Optional<ArrangeDeliveryResponseModel> arrangeDelivery(HttpHeaders httpHeaders) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        return Optional.ofNullable(
                restTemplate.exchange(
                        propsResolver.getArrangeDeliveryServiceEndpoint(),
                        HttpMethod.GET,
                        httpEntity,
                        ArrangeDeliveryResponseModel.class
                ).getBody()
        );
    }
}
