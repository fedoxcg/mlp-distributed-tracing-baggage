package com.fedox.study.tracing.delivery.aspects;

public class TracingConstants {

    private TracingConstants() {
    }

    public static final String LOGISTICS_TRANSPORT_FQDN = "com.fedox.study.tracing.delivery.services.EShopLogisticService.transport";
    public static final String ARRANGE_DELIVERY_CONTROLLER_FQDN = "com.fedox.study.tracing.delivery.api.EShopDelivery.arrangeDelivery";

    public static final String SPAN_APP_METHOD_TAG = "app.method";
    public static final String SPAN_APP_VERSION_TAG = "app.version";

    public static final String BAGGAGE_USER_TAG = "user";
}
