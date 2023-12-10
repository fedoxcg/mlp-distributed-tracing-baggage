package com.fedox.study.tracing.delivery.services;

import com.fedox.study.tracing.delivery.resolvers.ApplicationPropertiesResolver;
import com.fedox.study.tracing.delivery.responsemodels.LogisticTransportResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class EShopLogisticService {

    private ApplicationPropertiesResolver propsResolver;
    private RestTemplate restTemplate;

    @Autowired
    public EShopLogisticService(
            ApplicationPropertiesResolver propsResolver,
            RestTemplate restTemplate
    ) {
        this.propsResolver = propsResolver;
        this.restTemplate = restTemplate;
    }

    public Optional<LogisticTransportResponseModel> transport(HttpHeaders httpHeaders) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        return Optional.ofNullable(
                restTemplate.exchange(
                        propsResolver.getArrangeDeliveryServiceEndpoint(),
                        HttpMethod.GET,
                        httpEntity,
                        LogisticTransportResponseModel.class
                ).getBody()
        );
    }

}
