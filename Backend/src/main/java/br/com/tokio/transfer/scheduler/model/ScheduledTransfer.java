package br.com.tokio.transfer.scheduler.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ScheduledTransfer {

    @EqualsAndHashCode.Include
    private final Long id;

    private final String sourceAccount;
    private final String destinationAccount;
    private final BigDecimal amount;
    private final BigDecimal fee;
    private final LocalDate transferDate;
    private final LocalDate schedulingDate;

    /*
        Regras de agendamento para valores :
            - conta origem obrigatória
            - conta destino obrigatória
            - contas diferentes
            - valor maior que zero
            - datas obrigatórias
            - data de transferência não anterior à data de agendamento
            - taxa não negativa
     */
    public ScheduledTransfer(
            Long id,
            String sourceAccount,
            String destinationAccount,
            BigDecimal amount,
            BigDecimal fee,
            LocalDate transferDate,
            LocalDate schedulingDate
    ) {
        this.id = id;
        this.sourceAccount = requireNonBlank(sourceAccount, "sourceAccount");
        this.destinationAccount = requireNonBlank(destinationAccount, "destinationAccount");
        this.amount = requirePositive(amount, "amount");
        this.fee = requireNonNegative(fee, "fee");
        this.transferDate = Objects.requireNonNull(transferDate, "transferDate is required");
        this.schedulingDate = Objects.requireNonNull(schedulingDate, "schedulingDate is required");

        if (transferDate.isBefore(schedulingDate)) {
            throw new IllegalArgumentException("Transfer date cannot be before schedulingDate");
        }

        if (this.sourceAccount.equals(this.destinationAccount)) {
            throw new IllegalArgumentException("Source account and Destination account must be different");
        }
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value;
    }

    private static BigDecimal requirePositive(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " is required");

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero");
        }

        return value;
    }

    private static BigDecimal requireNonNegative(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " is required");

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }

        return value;
    }
}
