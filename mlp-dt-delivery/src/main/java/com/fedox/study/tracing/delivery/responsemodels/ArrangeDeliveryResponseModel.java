package com.fedox.study.tracing.delivery.responsemodels;

import com.fedox.study.tracing.delivery.dto.DeliveryArrangement;

import java.util.Map;
import java.util.Optional;

public record ArrangeDeliveryResponseModel(Integer httpStatusCode, Optional<DeliveryArrangement> deliveryArrangement, Map<String, String> errors) {
}
