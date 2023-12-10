package com.fedox.study.tracing.delivery.aspects;

import com.fedox.study.tracing.delivery.resolvers.ApplicationPropertiesResolver;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
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

    private ThreadLocal<String> userName = new ThreadLocal<>();

    private Tracer tracer;

    private ApplicationPropertiesResolver propsResolver;

    @Autowired
    public TracingAspect(Tracer tracer, ApplicationPropertiesResolver propsResolver) {
        this.tracer = tracer;
        this.propsResolver = propsResolver;
    }

    @Pointcut("execution(* com.fedox.study.tracing.delivery.api.EShopDelivery.arrangeDelivery(..))")
    public void arrangeDeliveryControllerCall() {}

    @Pointcut("execution(* com.fedox.study.tracing.delivery.services.EshopDeliveryService.arrangeDelivery(..))")
    public void arrangeDeliveryServiceCall() {}

    @Pointcut("execution(* com.fedox.study.tracing.delivery.services.EShopLogisticService.transport(..))")
    public void logisticTransportServiceCall() {}

    @Around("arrangeDeliveryServiceCall() || " +
            "logisticTransportServiceCall()")
    public Object traceServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        var methodFQDN = getFQDN(joinPoint);
        var span = tracer.buildSpan(joinPoint.getSignature().getName())
                .withTag(TracingConstants.SPAN_APP_METHOD_TAG, methodFQDN)
                .withTag(TracingConstants.SPAN_APP_VERSION_TAG, propsResolver.getAppVersion())
                .start()
                .setBaggageItem(TracingConstants.BAGGAGE_USER_TAG, this.userName.get());

        try (var scope = tracer.scopeManager().activate(span)) {
            return switch (methodFQDN) {
                case TracingConstants.LOGISTICS_TRANSPORT_FQDN -> injectTransportSpan(joinPoint, span);
                default -> joinPoint.proceed();
            };
        } finally {
            span.finish();
        }
    }

    @Around("arrangeDeliveryControllerCall()")
    public Object traceControllerCall(ProceedingJoinPoint joinPoint) throws Throwable  {
        var injectedSpanContext = extractSpanContext(joinPoint);
        HttpHeaders httpHeaders = (HttpHeaders) joinPoint.getArgs()[0];
        this.userName.set(extractUserNameFromHeader(httpHeaders).orElse("N/A"));

        if (Objects.nonNull(injectedSpanContext)) {
            return buildSpanFromContextAndProceed(injectedSpanContext, joinPoint);
        }

        log.debug("no context was injected on endpoint call");

        return buildSpanAndProceed(joinPoint);
    }

    private Object injectTransportSpan(ProceedingJoinPoint joinPoint, Span span) throws Throwable {
        span.setTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CLIENT);
        Tags.SPAN_KIND.set(tracer.activeSpan(), Tags.SPAN_KIND_CLIENT);
        Tags.HTTP_METHOD.set(tracer.activeSpan(), "GET");
        Tags.HTTP_URL.set(tracer.activeSpan(), propsResolver.getArrangeDeliveryServiceEndpoint());
        HttpHeaders httpHeaders = (HttpHeaders) joinPoint.getArgs()[0];

        HttpHeadersCarrier carrier = new HttpHeadersCarrier(httpHeaders);

        log.debug("inject trace: {}", span.context().toTraceId());

        tracer.inject(
                span.context(),
                Format.Builtin.TEXT_MAP,
                carrier
        );

        return joinPoint.proceed(new Object[] {httpHeaders});
    }

    private String getFQDN(ProceedingJoinPoint joinPoint) {
        return String.format("%s.%s",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
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
            this.userName.remove();
        }
    }

    private SpanContext extractSpanContext(ProceedingJoinPoint joinPoint) {
        HttpHeaders httpHeaders = (HttpHeaders) joinPoint.getArgs()[0];
        return tracer.extract(
                Format.Builtin.HTTP_HEADERS,
                new HttpHeadersCarrier(httpHeaders)
        );
    }

    private Optional<String> extractUserNameFromHeader(HttpHeaders httpHeaders) {
        String userName = "";
        if (Objects.nonNull(httpHeaders)) {
            var headers = httpHeaders.toSingleValueMap();
            userName = headers.getOrDefault("user", null);
        }
        return Optional.ofNullable(userName);
    }

    private Object buildSpanAndProceed(ProceedingJoinPoint joinPoint) throws Throwable {
        var span = tracer.buildSpan(joinPoint.getSignature().getName()).start();
        span.setTag(TracingConstants.SPAN_APP_METHOD_TAG, getFQDN(joinPoint));
        try (var scope = tracer.scopeManager().activate(span)) {
            return joinPoint.proceed();
        } finally {
            span.finish();
        }
    }
}
