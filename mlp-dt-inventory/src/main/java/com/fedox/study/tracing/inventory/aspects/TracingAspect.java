package com.fedox.study.tracing.inventory.aspects;

import com.fedox.study.tracing.inventory.resolvers.ApplicationPropertiesResolver;
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

    ThreadLocal<String> userName = new ThreadLocal<>();


    private final Tracer tracer;
    private final ApplicationPropertiesResolver propsResolver;

    @Autowired
    public TracingAspect(Tracer tracer, ApplicationPropertiesResolver propsResolver) {
        this.tracer = tracer;
        this.propsResolver = propsResolver;
    }

    @Pointcut("execution(* com.fedox.study.tracing.inventory.api.InventoryController.createOrder(..))")
    public void createOrderApiCall() {}

    @Pointcut("execution(* com.fedox.study.tracing.inventory.services.CRUDService.createOrder(..))")
    public void createOrderCRUDCall() {}

    @Around("createOrderApiCall()")
    public Object traceApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        var spanContext = extractSpanContext(joinPoint);
        this.userName.set(extractUserName(spanContext).orElse("N/A"));
        if (Objects.nonNull(spanContext)) {
            result = buildSpanFromContextAndProceed(spanContext, joinPoint);
        } else {
            log.debug("no context was injected on endpoint call");
            result = buildSpanAndProceed(joinPoint);
        }

        log.info("{}'s order creation", userName.get());

        return result;
    }

    private Optional<String> extractUserName(SpanContext spanContext) {
        if (Objects.nonNull(spanContext)) {
            for (var baggage : spanContext.baggageItems()) {
                if ("user".equals(baggage.getKey())) {
                    return Optional.of(baggage.getValue());
                }
            }
        }
        return Optional.empty();
    }

    @Around("createOrderCRUDCall()")
    public Object traceServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        var span = tracer.buildSpan(joinPoint.getSignature().getName())
                .withTag(TracingConstants.SPAN_APP_METHOD_TAG, getFQDN(joinPoint))
                .withTag(TracingConstants.SPAN_APP_VERSION_TAG, propsResolver.getAppVersion())
                .start()
                .setBaggageItem(TracingConstants.BAGGAGE_USER_TAG, this.userName.get());
        try (var scope = tracer.scopeManager().activate(span)) {
            return joinPoint.proceed();
        } finally {
            span.finish();
        }
    }

    private Object buildSpanFromContextAndProceed(SpanContext injectedSpanContext, ProceedingJoinPoint joinPoint) throws Throwable {
        var span = tracer.buildSpan(joinPoint.getSignature().getName())
                .asChildOf(injectedSpanContext)
                .withTag(TracingConstants.SPAN_APP_METHOD_TAG, getFQDN(joinPoint))
                .withTag(TracingConstants.SPAN_APP_VERSION_TAG, propsResolver.getAppVersion())
                .start()
                .setBaggageItem(TracingConstants.BAGGAGE_USER_TAG, this.userName.get());
        try (var scope = tracer.scopeManager().activate(span)) {
            return joinPoint.proceed();
        } finally {
            span.finish();
        }
    }

    private SpanContext extractSpanContext(ProceedingJoinPoint joinPoint) {
        HttpHeaders httpHeaders = (HttpHeaders) joinPoint.getArgs()[0];
        return tracer.extract(
                Format.Builtin.HTTP_HEADERS,
                new HttpHeadersCarrier(httpHeaders)
        );
    }

    private Object buildSpanAndProceed(ProceedingJoinPoint joinPoint) throws Throwable {
        var span = tracer.buildSpan(joinPoint.getSignature().getName())
                .withTag(TracingConstants.SPAN_APP_METHOD_TAG, getFQDN(joinPoint))
                .withTag(TracingConstants.SPAN_APP_VERSION_TAG, propsResolver.getAppVersion())
                .start()
                .setBaggageItem(TracingConstants.BAGGAGE_USER_TAG, this.userName.get());
        try (var scope = tracer.scopeManager().activate(span)) {
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

}
