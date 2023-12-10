package com.fedox.study.tracing.delivery.api;

import com.fedox.study.tracing.delivery.dto.ServiceStatus;
import com.fedox.study.tracing.delivery.responsemodels.ArrangeDeliveryResponseModel;
import com.fedox.study.tracing.delivery.services.EshopDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EShopDelivery {

    private EshopDeliveryService deliveryService;

    @Autowired
    public EShopDelivery(EshopDeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping(
            path = "/arrangeDelivery",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ArrangeDeliveryResponseModel arrangeDelivery(@RequestHeader HttpHeaders httpHeaders) {
        return deliveryService.arrangeDelivery(httpHeaders).orElse(null);
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
