package br.com.tokio.transfer.scheduler.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.requireNonNull;

@Service
public class TransferFeeCalculator {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private static final List<FeeRule> RULES = Arrays.asList(
            new FeeRule(0, 0, "3.00", "2.5"),
            new FeeRule(1, 10, "12.00", "0.0"),
            new FeeRule(11, 20, "0.00", "8.2"),
            new FeeRule(21, 30, "0.00", "6.9"),
            new FeeRule(31, 40, "0.00", "4.7"),
            new FeeRule(41, 50, "0.00", "1.7")
    );

    public BigDecimal calculate(
            BigDecimal amount,
            LocalDate schedulingDate,
            LocalDate transferDate
    ) {
        requireNonNull(amount, "amount is required");
        requireNonNull(schedulingDate, "schedulingDate is required");
        requireNonNull(transferDate, "transferDate is required");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        long daysBetween = DAYS.between(schedulingDate, transferDate);

        return RULES.stream()
                .filter(rule -> rule.appliesTo(daysBetween))
                .findFirst()
                .map(rule -> rule.calculate(amount))
                .orElseThrow(() -> new IllegalArgumentException("no applicable fee for this transfer date"));
    }

    private static class FeeRule {

        private final int startDay;
        private final int endDay;
        private final BigDecimal fixedFee;
        private final BigDecimal percentage;

        private FeeRule(int startDay, int endDay, String fixedFee, String percentage) {
            this.startDay = startDay;
            this.endDay = endDay;
            this.fixedFee = new BigDecimal(fixedFee);
            this.percentage = new BigDecimal(percentage);
        }

        private boolean appliesTo(long daysBetween) {
            return daysBetween >= startDay && daysBetween <= endDay;
        }

        private BigDecimal calculate(BigDecimal amount) {
            BigDecimal percentageFee = amount
                    .multiply(percentage)
                    .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);

            return fixedFee.add(percentageFee).setScale(2, RoundingMode.HALF_UP);
        }
    }
}