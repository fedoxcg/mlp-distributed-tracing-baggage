package com.fedox.study.tracing.logistic.services;

import com.fedox.study.tracing.logistic.dto.TransportStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransportService {

    public Optional<TransportStatus> transport(HttpHeaders httpHeaders) {
        return Optional.of(TransportStatus.PREPARATION);
    }

}
