package com.fedox.study.tracing.checkout.api;

import com.fedox.study.tracing.checkout.dto.ServiceStatus;
import com.fedox.study.tracing.checkout.formatters.ErrorMapFormatter;
import com.fedox.study.tracing.checkout.responsemodels.ArrangeDeliveryResponseModel;
import com.fedox.study.tracing.checkout.responsemodels.BillingPaymentResponseModel;
import com.fedox.study.tracing.checkout.responsemodels.InventoryOrderResponseModel;
import com.fedox.study.tracing.checkout.services.BillingService;
import com.fedox.study.tracing.checkout.services.DeliveryArrangementService;
import com.fedox.study.tracing.checkout.services.OrderService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.print.attribute.standard.Media;
import java.util.Optional;

@RestController
public class EShopCheckout {

    private final OrderService orderService;
    private final BillingService billingService;
    private final DeliveryArrangementService deliveryArrangementService;

    private final ErrorMapFormatter errorMapFormatter;

    @Autowired
    public EShopCheckout(OrderService orderService,
                         BillingService billingService,
                         DeliveryArrangementService deliveryArrangementService,
                         ErrorMapFormatter errorMapFormatter) {
        this.orderService = orderService;
        this.billingService = billingService;
        this.deliveryArrangementService = deliveryArrangementService;
        this.errorMapFormatter = errorMapFormatter;
    }

    @GetMapping(
            path = "/checkout",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    @ResponseBody
    public String checkout(@RequestHeader HttpHeaders httpHeaders) {
        Optional<InventoryOrderResponseModel> placedOrder = orderService.createOrder(httpHeaders);
        Optional<BillingPaymentResponseModel> payment = billingService.payment(httpHeaders);
        Optional<ArrangeDeliveryResponseModel> delivery = deliveryArrangementService.arrangeDelivery(httpHeaders);

        var responsesErrors = responsesErrorsExtraction(placedOrder, payment, delivery);
        if (!responsesErrors.isEmpty()) return responsesErrors;
        else {
            if (placedOrder.isPresent() && placedOrder.get().order().isPresent() &&
                payment.isPresent() && payment.get().paymentReceipt().isPresent() &&
                delivery.isPresent() && delivery.get().deliveryArrangement().isPresent()
            ) {
                return "Successfully placed order nr. " +
                        placedOrder.get().order().get().orderId() + " of amount " +
                        payment.get().paymentReceipt().get().amount() + " and shipping status " +
                        delivery.get().deliveryArrangement().get().status().name();
            } else {
                return "no order is placed " + errorMapFormatter.formatError(placedOrder.get());
            }
        }
    }

    private String responsesErrorsExtraction(Optional placeOrderRespModel, Optional paymentRespModel, Optional deliveryRespModel) {
        if (placeOrderRespModel.isEmpty()) return "no order is placed: no response from order service";
        if (paymentRespModel.isEmpty()) return "no order is placed: no response from payment service";
        if (deliveryRespModel.isEmpty()) return "no order is placed: no response from delivery service";
        return Strings.EMPTY;
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
