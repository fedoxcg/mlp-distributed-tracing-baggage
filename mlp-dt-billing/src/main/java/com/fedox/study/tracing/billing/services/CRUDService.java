package com.fedox.study.tracing.billing.services;

import com.fedox.study.tracing.billing.dto.PaymentReceipt;
import com.fedox.study.tracing.billing.dto.PaymentStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CRUDService {
    public Optional<PaymentReceipt> pay() {
        return Optional.of(new PaymentReceipt(
                BigDecimal.valueOf(1000.00),
                LocalDate.now(),
                PaymentStatus.OK
        ));
    }

}
