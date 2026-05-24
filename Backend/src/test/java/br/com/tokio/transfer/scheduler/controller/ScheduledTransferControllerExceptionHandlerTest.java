package br.com.tokio.transfer.scheduler.controller;

import br.com.tokio.transfer.scheduler.controller.mapper.ScheduledTransferControllerMapper;
import br.com.tokio.transfer.scheduler.service.ScheduledTransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasKey;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduledTransferController.class)
@DisplayName("Tratamento de erros HTTP da API de agendamento")
class ScheduledTransferControllerExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduledTransferService scheduledTransferService;

    @MockBean
    private ScheduledTransferControllerMapper mapper;

    @Test
    @DisplayName("deve retornar 400 quando nao houver taxa aplicavel para a data de transferencia")
    void shouldReturnBadRequestWhenThereIsNoApplicableFee() throws Exception {
        when(scheduledTransferService.schedule(
                eq("1234567890"),
                eq("0987654321"),
                eq(new BigDecimal("1000.00")),
                eq(LocalDate.of(2026, 7, 14))
        )).thenThrow(new IllegalArgumentException("no applicable fee for this transfer date"));

        mockMvc.perform(post("/scheduled-transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceAccount\":\"1234567890\",\"destinationAccount\":\"0987654321\",\"amount\":1000.00,\"transferDate\":\"2026-07-14\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("no applicable fee for this transfer date"))
                .andExpect(jsonPath("$.path").value("/scheduled-transfers"));
    }

    @Test
    @DisplayName("deve retornar 400 com erros de campo quando o request for invalido")
    void shouldReturnBadRequestWithFieldErrorsWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/scheduled-transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceAccount\":\"123\",\"destinationAccount\":\"\",\"amount\":0,\"transferDate\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid request fields"))
                .andExpect(jsonPath("$.path").value("/scheduled-transfers"))
                .andExpect(jsonPath("$.fieldErrors", hasKey("sourceAccount")))
                .andExpect(jsonPath("$.fieldErrors", hasKey("destinationAccount")))
                .andExpect(jsonPath("$.fieldErrors", hasKey("amount")))
                .andExpect(jsonPath("$.fieldErrors", hasKey("transferDate")));

        verify(scheduledTransferService, never()).schedule(any(), any(), any(), any());
    }

    @Test
    @DisplayName("deve retornar 500 quando ocorrer uma falha inesperada")
    void shouldReturnInternalServerErrorWhenUnexpectedErrorHappens() throws Exception {
        when(scheduledTransferService.findAll())
                .thenThrow(new RuntimeException("database unavailable"));

        mockMvc.perform(get("/scheduled-transfers"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Unexpected internal server error : database unavailable"))
                .andExpect(jsonPath("$.path").value("/scheduled-transfers"));
    }
}
