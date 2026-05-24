package br.com.tokio.transfer.scheduler.service;

import br.com.tokio.transfer.scheduler.repository.mapper.ScheduledTransferMapper;
import br.com.tokio.transfer.scheduler.model.ScheduledTransfer;
import br.com.tokio.transfer.scheduler.repository.ScheduledTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ScheduledTransferService {

    private final TransferFeeCalculator transferFeeCalculator;
    private final ScheduledTransferRepository scheduledTransferRepository;
    private final ScheduledTransferMapper scheduledTransferMapper;
    private final Clock clock = Clock.systemDefaultZone();

    public ScheduledTransfer schedule(
            String sourceAccount,
            String destinationAccount,
            BigDecimal amount,
            LocalDate transferDate
    ) {
        LocalDate schedulingDate = LocalDate.now(clock);

        BigDecimal fee = transferFeeCalculator.calculate(
                amount,
                schedulingDate,
                transferDate
        );

        ScheduledTransfer scheduledTransfer = new ScheduledTransfer(
                null,
                sourceAccount,
                destinationAccount,
                amount,
                fee,
                transferDate,
                schedulingDate
        );

        return scheduledTransferMapper.toDomain(
                scheduledTransferRepository.save(
                        scheduledTransferMapper.toEntity(scheduledTransfer)
                )
        );
    }
}
