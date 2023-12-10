package com.fedox.study.tracing.billing.resolvers;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApplicationPropertiesResolver {

    @Value("${app.version}")
    private String appVersion;
}
