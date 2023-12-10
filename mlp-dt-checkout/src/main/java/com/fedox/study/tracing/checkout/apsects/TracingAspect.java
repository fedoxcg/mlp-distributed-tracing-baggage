package com.fedox.study.tracing.checkout.apsects;

import com.fedox.study.tracing.checkout.resolvers.ApplicationPropertiesResolver;
import io.opentracing.Span;
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

    private final ThreadLocal<String> userName = new ThreadLocal<>();

    private final Tracer tracer;
    private final ApplicationPropertiesResolver propsResolver;

    @Autowired
    public TracingAspect(Tracer tracer, ApplicationPropertiesResolver propsResolver) {
        this.tracer = tracer;
        this.propsResolver = propsResolver;
    }

    @Pointcut("execution(* com.fedox.study.tracing.checkout.services.BillingService.payment(..))")
    public void paymentServiceCall() {}

    @Pointcut("execution(* com.fedox.study.tracing.checkout.services.OrderService.createOrder(..))")
    public void createOrderServiceCall() {}

    @Pointcut("execution(* com.fedox.study.tracing.checkout.api.EShopCheckout.checkout(..))")
    public void checkoutApiCall() {}

    @Pointcut("execution(* com.fedox.study.tracing.checkout.services.DeliveryArrangementService.arrangeDelivery(..))")
    public void arrangeDeliveryCall() {}

    @Around("checkoutApiCall()")
    public Object traceServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpHeaders httpHeaders = (HttpHeaders) joinPoint.getArgs()[0];
        this.userName.set(extractUserNameFromHeader(httpHeaders).orElse("N/A"));
        var span = tracer.buildSpan(joinPoint.getSignature().getName())
                .withTag(TracingConstants.SPAN_APP_METHOD_TAG, joinPoint.getSignature().getDeclaringTypeName())
                .withTag(TracingConstants.SPAN_APP_VERSION_TAG, propsResolver.getAppVersion())
                .start()
                .setBaggageItem(TracingConstants.BAGGAGE_USER_TAG, this.userName.get());
        try (var scope = tracer.scopeManager().activate(span)) {
            return joinPoint.proceed();
        } finally {
            span.finish();
            userName.remove();
        }
    }

    private Optional<String> extractUserNameFromHeader(HttpHeaders httpHeaders) {
        String userName = "";
        if (Objects.nonNull(httpHeaders)) {
            var headers = httpHeaders.toSingleValueMap();
            userName = headers.getOrDefault("user", null);
        }
        return Optional.ofNullable(userName);
    }

    @Around("createOrderServiceCall() || " +
            "paymentServiceCall() || " +
            "arrangeDeliveryCall()")
    public Object traceSubServicesCall(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("tracing {} START", joinPoint.getSignature().getName());

        String fqdn = getFQDN(joinPoint);
        var span = tracer.buildSpan(joinPoint.getSignature().getName())
                .withTag(TracingConstants.SPAN_APP_METHOD_TAG, joinPoint.getSignature().getDeclaringTypeName())
                .withTag(TracingConstants.SPAN_APP_VERSION_TAG, propsResolver.getAppVersion())
                .start()
                .setBaggageItem(TracingConstants.BAGGAGE_USER_TAG, this.userName.get());

        log.debug("fqdn: {}", fqdn);

        try (var scope = tracer.scopeManager().activate(span)) {
            return switch (fqdn) {
                case TracingConstants.ORDER_SVC_CREATE_ORDER_FQDN -> injectSpanContext(joinPoint, tracer.activeSpan(), propsResolver.getOrderServiceEndpoint());
                case TracingConstants.BILLING_SVC_PAYMENT_FQDN -> injectSpanContext(joinPoint, tracer.activeSpan(), propsResolver.getBillingServicePaymentEndpoint());
                case TracingConstants.DELIVERY_SVC_ARRANGE_FQDN -> injectSpanContext(joinPoint, tracer.activeSpan(), propsResolver.getArrangeDeliveryServiceEndpoint());
                default -> joinPoint.proceed();
            };
        } finally {
            span.finish();
            log.debug("tracing {} END", joinPoint.getSignature().getName());
        }
    }

    private Object injectSpanContext(ProceedingJoinPoint joinPoint, Span span, String endpoint) throws Throwable {
        log.debug("inject {} span context from {} START", span.context().toTraceId(), joinPoint.getSignature().getName());

        span.setTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CLIENT);
        Tags.SPAN_KIND.set(tracer.activeSpan(), Tags.SPAN_KIND_CLIENT);
        Tags.HTTP_METHOD.set(tracer.activeSpan(), "GET");
        Tags.HTTP_URL.set(tracer.activeSpan(), endpoint);

        HttpHeaders httpHeaders = (HttpHeaders) joinPoint.getArgs()[0];
        HttpHeadersCarrier carrier = new HttpHeadersCarrier(httpHeaders);

        log.debug("inject trace: {}", span.context().toTraceId());

        tracer.inject(
                span.context(),
                Format.Builtin.TEXT_MAP,
                carrier
        );

        log.debug("inject {} span context from {} END", span.context().toTraceId(), joinPoint.getSignature().getName());

        return joinPoint.proceed(new Object[] {httpHeaders});
    }

    private String getFQDN(ProceedingJoinPoint joinPoint) {
        return String.format("%s.%s",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

}
