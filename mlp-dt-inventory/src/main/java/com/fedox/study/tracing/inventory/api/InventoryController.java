package com.fedox.study.tracing.inventory.api;

import com.fedox.study.tracing.inventory.dto.ServiceStatus;
import com.fedox.study.tracing.inventory.responsemodels.OrderResponseModel;
import com.fedox.study.tracing.inventory.services.CRUDService;
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
public class InventoryController {

    private CRUDService crudService;

    @Autowired
    public InventoryController(CRUDService crudService) {
        this.crudService = crudService;
    }

    @GetMapping(
            path = "/createOrder",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderResponseModel createOrder(@RequestHeader HttpHeaders httpHeaders) {
        var createOrderResponse = crudService.createOrder();
        return createOrderResponse.map(order -> new OrderResponseModel(
                HttpStatus.OK.value(),
                order,
                null
        )).orElseGet(() -> new OrderResponseModel(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null,
                Map.of("Generic Error", "Order creation error")
        ));
    }

    @GetMapping(
            path = "/healthcheck",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ServiceStatus healthcheck() {
        return new ServiceStatus("UP");
    }
}
