package com.fedox.study.tracing.checkout.responsemodels;

import com.fedox.study.tracing.checkout.dto.DeliveryArrangement;

import java.util.Map;
import java.util.Optional;

public record ArrangeDeliveryResponseModel(Integer httpStatusCode, Optional<DeliveryArrangement> deliveryArrangement, Map<String, String> errors) {
}
