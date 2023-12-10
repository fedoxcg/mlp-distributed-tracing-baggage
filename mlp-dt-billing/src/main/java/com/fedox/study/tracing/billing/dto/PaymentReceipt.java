package com.fedox.study.tracing.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentReceipt(BigDecimal amount, LocalDate date, PaymentStatus status) {
}
