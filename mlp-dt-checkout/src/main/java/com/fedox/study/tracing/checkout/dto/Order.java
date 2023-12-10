package com.fedox.study.tracing.checkout.dto;

import java.time.LocalDateTime;
import java.util.UUID;


public record Order(UUID orderId, LocalDateTime orderDate) {
}
