package com.fedox.study.tracing.checkout.formatters;

import com.fedox.study.tracing.checkout.responsemodels.InventoryOrderResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Optional;

@Slf4j
class ErrorMapFormatterTest {

    private final ErrorMapFormatter formatter = new ErrorMapFormatter();

    @Test
    @DisplayName("format a single create order error")
    void formatError() {
        log.info("format a single create order error: STARTED");

        InventoryOrderResponseModel inventoryOrderResponseModel = new InventoryOrderResponseModel(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Optional.empty(),
                Map.of("Internal Server Error", "Internal Server Error")
        );

        String formattedError = formatter.formatError(inventoryOrderResponseModel);

        Assertions.assertNotNull(formattedError);
        Assertions.assertFalse(formattedError.isBlank());
        Assertions.assertEquals("Internal Server Error|Internal Server Error;", formattedError);

        log.info("format a single create order error: FINISHED");
    }

    @Test
    @DisplayName("format a multi create order errors")
    void formatMultipleErrors() {
        log.info("format a multi create order errors: STARTED");

        final var ERROR_1 = "Internal Server Error|Internal Server Error;";
        final var ERROR_2 = "SNO|Supplier Not Exists;";

        InventoryOrderResponseModel inventoryOrderResponseModel = new InventoryOrderResponseModel(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Optional.empty(),
                Map.of("Internal Server Error", "Internal Server Error",
                        "SNO", "Supplier Not Exists")
        );

        String formattedError = formatter.formatError(inventoryOrderResponseModel);

        Assertions.assertNotNull(formattedError);
        Assertions.assertFalse(formattedError.isBlank());
        Assertions.assertTrue(formattedError.contains(ERROR_1));
        Assertions.assertTrue(formattedError.contains(ERROR_2));
        Assertions.assertEquals(ERROR_1.concat(ERROR_2).length(), formattedError.length());

        log.info("format a multi create order errors: FINISHED");
    }
}