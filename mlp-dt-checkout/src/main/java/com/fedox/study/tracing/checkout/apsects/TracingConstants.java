package com.fedox.study.tracing.checkout.apsects;

public class TracingConstants {

    private TracingConstants() {
    }

    public static final String ORDER_SVC_CREATE_ORDER_FQDN = "com.fedox.study.tracing.checkout.services.OrderService.createOrder";
    public static final String BILLING_SVC_PAYMENT_FQDN = "com.fedox.study.tracing.checkout.services.BillingService.payment";
    public static final String DELIVERY_SVC_ARRANGE_FQDN = "com.fedox.study.tracing.checkout.services.DeliveryArrangementService.arrangeDelivery";

    public static final String SPAN_APP_METHOD_TAG = "app.method";
    public static final String SPAN_APP_VERSION_TAG = "app.version";

    public static final String BAGGAGE_USER_TAG = "user";
}
