package com.fedox.study.tracing.delivery.responsemodels;

import java.util.Map;

public record LogisticTransportResponseModel(Integer httpStatus, LogisticTransportStatus status, Map<String, String> errors) {
}
