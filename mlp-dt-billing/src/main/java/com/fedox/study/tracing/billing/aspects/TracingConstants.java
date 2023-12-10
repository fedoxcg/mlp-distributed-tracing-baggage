package com.fedox.study.tracing.billing.aspects;

public class TracingConstants {

    private TracingConstants() {
    }

    public static final String SPAN_APP_METHOD_TAG = "app.method";
    public static final String SPAN_APP_VERSION_TAG = "app.version";

    public static final String BAGGAGE_USER_TAG = "user";
}
