package com.fedox.study.tracing.delivery.resolvers;

import lombok.Getter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApplicationPropertiesResolver {

    @Value("${app.version}")
    private String appVersion;

    @Value("${services.logistic.transportEndpoint}")
    private String arrangeDeliveryServiceEndpoint;

}
