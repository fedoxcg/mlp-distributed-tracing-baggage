package com.fedox.study.tracing.logistic.api;

import com.fedox.study.tracing.logistic.dto.ServiceStatus;
import com.fedox.study.tracing.logistic.responsemodels.TransportResponseModel;
import com.fedox.study.tracing.logistic.services.TransportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class EShopLogistic {

    private TransportService transportService;

    @Autowired
    public EShopLogistic(TransportService transportService) {
        this.transportService = transportService;
    }

    @GetMapping(
            path = "/transport",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public TransportResponseModel transport(@RequestHeader HttpHeaders httpHeaders) {
        httpHeaders.forEach((k, v) -> log.debug("{}: {}", k, v));
        var transportResponse = transportService.transport(httpHeaders);
        if (transportResponse.isPresent()) {
            return new TransportResponseModel(
                    HttpStatus.OK.value(),
                    transportResponse.orElse(null),
                    null
            );
        } else {
            return new TransportResponseModel(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    transportResponse.orElse(null),
                    Map.of("ISR", "Internal Server Error")
            );
        }
    }

    @GetMapping(
            path = "/healthcheck",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ServiceStatus healthCheck() {
        return new ServiceStatus("OK");
    }

}
