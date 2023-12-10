package com.fedox.study.tracing.billing.api;


import com.fedox.study.tracing.billing.dto.ServiceStatus;
import com.fedox.study.tracing.billing.responsemodels.BillingPaymentResponseModel;
import com.fedox.study.tracing.billing.services.CRUDService;
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
public class BillingController {

    private CRUDService crudService;

    @Autowired
    public BillingController(CRUDService crudService) {
        this.crudService = crudService;
    }

    @GetMapping(
            path = "/payment",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public BillingPaymentResponseModel payment(@RequestHeader HttpHeaders httpHeaders) {
        var paymentReceipt = crudService.pay();
        if (paymentReceipt.isPresent()) {
            return new BillingPaymentResponseModel(
                    HttpStatus.OK.value(),
                    paymentReceipt,
                    null
            );
        }
        return new BillingPaymentResponseModel(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null,
                Map.of("Generic Error", "Payment error")
        );
    }

    @GetMapping(
            path = "/healthcheck",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ServiceStatus healthcheck() {
        return new ServiceStatus("OK");
    }


}
