package com.fedox.study.tracing.delivery.services;

import com.fedox.study.tracing.delivery.dto.DeliveryArrangement;
import com.fedox.study.tracing.delivery.dto.DeliveryArrangementStatus;
import com.fedox.study.tracing.delivery.responsemodels.ArrangeDeliveryResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class EshopDeliveryService {

    private EShopLogisticService logisticService;

    @Autowired
    public EshopDeliveryService(EShopLogisticService logisticService) {
        this.logisticService = logisticService;
    }

    public Optional<ArrangeDeliveryResponseModel> arrangeDelivery(HttpHeaders httpHeaders) {
        if (logisticService.transport(httpHeaders).isPresent()) {
            return Optional.of(
                    new ArrangeDeliveryResponseModel(
                            HttpStatus.CREATED.value(),
                            Optional.of(new DeliveryArrangement(
                                    "SDA",
                                    DeliveryArrangementStatus.PREPARED)),
                            null
                    )
            );
        } else {
            return Optional.of(
                    new ArrangeDeliveryResponseModel(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            null,
                            Map.of("ISE", "Internal Server Error")
                    )
            );
        }
    }

}
