package com.fedox.study.tracing.billing.responsemodels;

import com.fedox.study.tracing.billing.dto.PaymentReceipt;

import java.util.Map;
import java.util.Optional;

public record BillingPaymentResponseModel(Integer httpStatusCode, Optional<PaymentReceipt> paymentReceipt, Map<String, String> errors) {
}
