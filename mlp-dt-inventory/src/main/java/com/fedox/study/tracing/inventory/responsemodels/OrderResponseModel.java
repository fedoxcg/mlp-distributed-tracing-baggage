package com.fedox.study.tracing.inventory.responsemodels;

import com.fedox.study.tracing.inventory.dto.Order;

import java.util.Map;

public record OrderResponseModel(Integer httpStatusCode, Order order, Map<String, String> errors) {
}
