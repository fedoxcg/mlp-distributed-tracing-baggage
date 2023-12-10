package com.fedox.study.tracing.checkout.apsects;

import io.opentracing.propagation.TextMap;
import org.springframework.http.HttpHeaders;

import java.util.Iterator;
import java.util.Map;

public class HttpHeadersCarrier implements TextMap {

    private final HttpHeaders httpHeaders;

    public HttpHeadersCarrier(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return httpHeaders.toSingleValueMap().entrySet().iterator();
    }

    @Override
    public void put(String key, String value) {
        this.httpHeaders.set(key, value);
    }
}
