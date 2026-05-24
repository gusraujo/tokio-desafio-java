package br.com.tokio.transfer.scheduler.controller;

import br.com.tokio.transfer.scheduler.controller.dto.ScheduleTransferRequest;
import br.com.tokio.transfer.scheduler.controller.dto.ScheduledTransferResponse;
import br.com.tokio.transfer.scheduler.controller.mapper.ScheduledTransferControllerMapper;
import br.com.tokio.transfer.scheduler.model.ScheduledTransfer;
import br.com.tokio.transfer.scheduler.service.ScheduledTransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("API de agendamento de transferencias")
class ScheduledTransferControllerTest {

    @Mock
    private ScheduledTransferService scheduledTransferService;

    @Mock
    private ScheduledTransferControllerMapper mapper;

    @InjectMocks
    private ScheduledTransferController controller;

    @Test
    @DisplayName("deve permitir que o usuario agende uma transferencia financeira")
    void shouldAllowUserToScheduleFinancialTransfer() {
        LocalDate transferDate = LocalDate.of(2026, 6, 3);
        LocalDate schedulingDate = LocalDate.of(2026, 5, 24);
        ScheduleTransferRequest request = new ScheduleTransferRequest(
                "1234567890",
                "0987654321",
                new BigDecimal("1000.00"),
                transferDate
        );
        ScheduledTransfer scheduledTransfer = new ScheduledTransfer(
                1L,
                "1234567890",
                "0987654321",
                new BigDecimal("1000.00"),
                new BigDecimal("12.00"),
                transferDate,
                schedulingDate
        );
        ScheduledTransferResponse expectedResponse = new ScheduledTransferResponse(
                1L,
                "1234567890",
                "0987654321",
                new BigDecimal("1000.00"),
                new BigDecimal("12.00"),
                new BigDecimal("1012.00"),
                transferDate,
                schedulingDate
        );

        when(scheduledTransferService.schedule(
                request.getSourceAccount(),
                request.getDestinationAccount(),
                request.getAmount(),
                request.getTransferDate()
        )).thenReturn(scheduledTransfer);
        when(mapper.toResponse(scheduledTransfer)).thenReturn(expectedResponse);

        ScheduledTransferResponse response = controller.schedule(request);

        assertAll(
                () -> assertEquals(1L, response.getId()),
                () -> assertEquals("1234567890", response.getSourceAccount()),
                () -> assertEquals("0987654321", response.getDestinationAccount()),
                () -> assertEquals(new BigDecimal("1000.00"), response.getAmount()),
                () -> assertEquals(new BigDecimal("12.00"), response.getFee()),
                () -> assertEquals(new BigDecimal("1012.00"), response.getTotalAmount()),
                () -> assertEquals(transferDate, response.getTransferDate()),
                () -> assertEquals(schedulingDate, response.getSchedulingDate())
        );
        verify(scheduledTransferService).schedule(
                "1234567890",
                "0987654321",
                new BigDecimal("1000.00"),
                transferDate
        );
        verify(mapper).toResponse(scheduledTransfer);
    }

    @Test
    @DisplayName("deve permitir que o usuario consulte o extrato de agendamentos cadastrados")
    void shouldAllowUserToViewStatementWithAllScheduledTransfers() {
        LocalDate transferDate = LocalDate.of(2026, 6, 3);
        LocalDate schedulingDate = LocalDate.of(2026, 5, 24);
        ScheduledTransfer scheduledTransfer = new ScheduledTransfer(
                1L,
                "1234567890",
                "0987654321",
                new BigDecimal("1000.00"),
                new BigDecimal("12.00"),
                transferDate,
                schedulingDate
        );
        ScheduledTransferResponse expectedResponse = new ScheduledTransferResponse(
                1L,
                "1234567890",
                "0987654321",
                new BigDecimal("1000.00"),
                new BigDecimal("12.00"),
                new BigDecimal("1012.00"),
                transferDate,
                schedulingDate
        );

        when(scheduledTransferService.findAll()).thenReturn(Collections.singletonList(scheduledTransfer));
        when(mapper.toResponse(scheduledTransfer)).thenReturn(expectedResponse);

        List<ScheduledTransferResponse> responses = controller.findAll();

        assertEquals(1, responses.size());
        assertAll(
                () -> assertEquals(1L, responses.get(0).getId()),
                () -> assertEquals("1234567890", responses.get(0).getSourceAccount()),
                () -> assertEquals("0987654321", responses.get(0).getDestinationAccount()),
                () -> assertEquals(new BigDecimal("1000.00"), responses.get(0).getAmount()),
                () -> assertEquals(new BigDecimal("12.00"), responses.get(0).getFee()),
                () -> assertEquals(new BigDecimal("1012.00"), responses.get(0).getTotalAmount()),
                () -> assertEquals(transferDate, responses.get(0).getTransferDate()),
                () -> assertEquals(schedulingDate, responses.get(0).getSchedulingDate())
        );
        verify(scheduledTransferService).findAll();
        verify(mapper).toResponse(scheduledTransfer);
    }
}
