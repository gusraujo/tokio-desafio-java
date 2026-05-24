package br.com.tokio.transfer.scheduler.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Transfer fee calculator")
class TransferFeeCalculatorTest {

    private final TransferFeeCalculator calculator = new TransferFeeCalculator();

    @ParameterizedTest(name = "should calculate fee {2} when transfer is scheduled after {0} days")
    @CsvSource({
            "0, 1000.00, 28.00",
            "1, 1000.00, 12.00",
            "10, 1000.00, 12.00",
            "11, 1000.00, 82.00",
            "20, 1000.00, 82.00",
            "21, 1000.00, 69.00",
            "30, 1000.00, 69.00",
            "31, 1000.00, 47.00",
            "40, 1000.00, 47.00",
            "41, 1000.00, 17.00",
            "50, 1000.00, 17.00"
    })
    void calculate_shouldReturnFee_whenTransferDateHasApplicableRule(
            int daysAfterScheduling,
            String amount,
            String expectedFee
    ) {
        LocalDate schedulingDate = LocalDate.of(2026, 5, 24);
        LocalDate transferDate = schedulingDate.plusDays(daysAfterScheduling);

        BigDecimal fee = calculator.calculate(
                new BigDecimal(amount),
                schedulingDate,
                transferDate
        );

        assertEquals(new BigDecimal(expectedFee), fee);
    }

    @Test
    void calculate_shouldThrowException_whenTransferDateHasNoApplicableRule() {
        LocalDate schedulingDate = LocalDate.of(2026, 5, 24);
        LocalDate transferDate = schedulingDate.plusDays(51);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(new BigDecimal("1000.00"), schedulingDate, transferDate)
        );

        assertEquals("no applicable fee for this transfer date", exception.getMessage());
    }

    @Test
    void calculate_shouldThrowException_whenAmountIsZero() {
        LocalDate schedulingDate = LocalDate.of(2026, 5, 24);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(BigDecimal.ZERO, schedulingDate, schedulingDate)
        );

        assertEquals("amount must be greater than zero", exception.getMessage());
    }

    @Test
    void calculate_shouldThrowException_whenAmountIsNegative() {
        LocalDate schedulingDate = LocalDate.of(2026, 5, 24);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(new BigDecimal("-1.00"), schedulingDate, schedulingDate)
        );

        assertEquals("amount must be greater than zero", exception.getMessage());
    }
}
