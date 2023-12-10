package com.fedox.study.tracing.checkout.formatters;

import com.fedox.study.tracing.checkout.responsemodels.InventoryOrderResponseModel;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ErrorMapFormatter {

    public String formatError(InventoryOrderResponseModel inventoryOrderResponseModel) {
        StringBuilder sb = new StringBuilder();
        inventoryOrderResponseModel
                .errors()
                .forEach((key, value) -> sb.append(key.concat("|").concat(value).concat(";")));
        return sb.toString();
    }

}
