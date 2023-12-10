package com.fedox.study.tracing.inventory.aspects;

public class TracingConstants {

    private TracingConstants() {
    }

    //SPAN TAGS
    public static final String SPAN_APP_METHOD_TAG = "app.method";
    public static final String SPAN_APP_VERSION_TAG = "app.version";

    //SPAN BAGGAGES
    public static final String BAGGAGE_USER_TAG = "user";
}
