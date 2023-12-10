package com.fedox.study.tracing.logistic.aspects;

import com.fedox.study.tracing.logistic.resolvers.ApplicationPropertiesResolver;
import io.jaegertracing.internal.utils.Http;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@Aspect
@Slf4j
public class TracingAspect {

    private final ThreadLocal<String> userName = new ThreadLocal<>();

    private final Tracer tracer;

    private final ApplicationPropertiesResolver propsResolver;

    @Autowired
    public TracingAspect(Tracer tracer, ApplicationPropertiesResolver propsResolver) {
        this.tracer = tracer;
        this.propsResolver = propsResolver;
    }

    @Pointcut("execution(* com.fedox.study.tracing.logistic.api.EShopLogistic.transport(..))")
    public void transportControllerCall() {}

    @Pointcut("execution(* com.fedox.study.tracing.logistic.services.TransportService.transport(..))")
    public void transportCall() {}

    @Around("transportControllerCall()")
    public Object traceControllerCall(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpHeaders httpHeaders = (HttpHeaders) joinPoint.getArgs()[0];
        var spanContext = extractSpanContext(httpHeaders);
        if (Objects.nonNull(spanContext)) log.debug("received trace: {}", spanContext.toTraceId());
        this.userName.set(extractUserNameFromHeader(httpHeaders).orElse("N/A"));
        var span = tracer.buildSpan(joinPoint.getSignature().getName())
                .asChildOf(spanContext)
                .withTag(TracingConstants.SPAN_APP_METHOD_TAG, getFQDN(joinPoint))
                .withTag(TracingConstants.SPAN_APP_VERSION_TAG, propsResolver.getAppVersion())
                .start()
                .setBaggageItem(TracingConstants.BAGGAGE_USER_TAG, this.userName.get());
        try (var scope = tracer.scopeManager().activate(span)) {
            return joinPoint.proceed();
        } finally {
            span.finish();
            this.userName.remove();
        }
    }

    private SpanContext extractSpanContext(HttpHeaders httpHeaders) {
        return tracer.extract(
                Format.Builtin.TEXT_MAP,
                new HttpHeadersCarrier(httpHeaders)
        );
    }

    @Around("transportCall()")
    public Object traceServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        var span = tracer.buildSpan(joinPoint.getSignature().getName())
                .withTag(TracingConstants.SPAN_APP_METHOD_TAG, getFQDN(joinPoint))
                .withTag(TracingConstants.SPAN_APP_VERSION_TAG, propsResolver.getAppVersion())
                .start()
                .setBaggageItem(TracingConstants.BAGGAGE_USER_TAG, this.userName.get());
        try (var scope = tracer.scopeManager().activate(span)) {
            log.debug("{}'s transport preparation", this.userName.get());

            return joinPoint.proceed();
        } finally {
            span.finish();
        }
    }

    private String getFQDN(ProceedingJoinPoint joinPoint) {
        return String.format("%s.%s",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

    private Optional<String> extractUserNameFromHeader(HttpHeaders httpHeaders) {
        String userName = "";
        if (Objects.nonNull(httpHeaders)) {
            var headers = httpHeaders.toSingleValueMap();
            userName = headers.getOrDefault("user", null);
        }
        return Optional.ofNullable(userName);
    }
}
