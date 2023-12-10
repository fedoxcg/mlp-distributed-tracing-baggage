package com.fedox.study.tracing.checkout.responsemodels;

import com.fedox.study.tracing.checkout.dto.Order;

import java.util.Map;
import java.util.Optional;

public record InventoryOrderResponseModel(Integer httpStatusCode, Optional<Order> order, Map<String, String> errors) {
}
