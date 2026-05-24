package br.com.tokio.transfer.scheduler.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleTransferRequest {

    @NotBlank
    @Pattern(regexp = "\\d{10}")
    private String sourceAccount;

    @NotBlank
    @Pattern(regexp = "\\d{10}")
    private String destinationAccount;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private LocalDate transferDate;
}
