package br.com.tokio.transfer.scheduler.service;

import br.com.tokio.transfer.scheduler.model.ScheduledTransfer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Scheduled transfer service")
class ScheduledTransferServiceTest {

    private final ScheduledTransferService service = new ScheduledTransferService(
            new TransferFeeCalculator()
    );

    @Test
    void schedule_shouldCreateScheduledTransferWithCalculatedFee() {
        LocalDate today = LocalDate.now();
        LocalDate transferDate = today.plusDays(10);

        ScheduledTransfer scheduledTransfer = service.schedule(
                "1234567890",
                "0987654321",
                new BigDecimal("1000.00"),
                transferDate
        );

        assertEquals("1234567890", scheduledTransfer.getSourceAccount());
        assertEquals("0987654321", scheduledTransfer.getDestinationAccount());
        assertEquals(new BigDecimal("1000.00"), scheduledTransfer.getAmount());
        assertEquals(new BigDecimal("12.00"), scheduledTransfer.getFee());
        assertEquals(transferDate, scheduledTransfer.getTransferDate());
        assertEquals(today, scheduledTransfer.getSchedulingDate());
    }

    @Test
    void schedule_shouldThrowException_whenTransferDateHasNoApplicableFee() {
        LocalDate transferDate = LocalDate.now().plusDays(51);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.schedule(
                        "1234567890",
                        "0987654321",
                        new BigDecimal("1000.00"),
                        transferDate
                )
        );

        assertEquals("no applicable fee for this transfer date", exception.getMessage());
    }
}
