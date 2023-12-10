package com.fedox.study.tracing.logistic;

import com.fedox.study.tracing.logistic.api.EShopLogistic;
import com.fedox.study.tracing.logistic.services.TransportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        EShopLogistic.class
})
@Import({TransportService.class})
class EShopLogisticTest {

    @Autowired
    private EShopLogistic eShopController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void transport() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(get("/transport")
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("{\"httpStatus\":200,\"status\":\"PREPARATION\",\"errors\":null}"))
                .andDo(print())
                .andReturn();
    }
}