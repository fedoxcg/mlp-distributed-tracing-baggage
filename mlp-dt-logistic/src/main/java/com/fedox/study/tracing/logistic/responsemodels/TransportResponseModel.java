package com.fedox.study.tracing.logistic.responsemodels;

import com.fedox.study.tracing.logistic.dto.TransportStatus;

import java.util.Map;

public record TransportResponseModel(Integer httpStatus, TransportStatus status, Map<String, String> errors) {
}
