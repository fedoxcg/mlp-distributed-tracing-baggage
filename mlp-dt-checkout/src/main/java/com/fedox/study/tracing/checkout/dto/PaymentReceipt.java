package com.fedox.study.tracing.checkout.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentReceipt(BigDecimal amount, LocalDate date, PaymentStatus status) {
}
