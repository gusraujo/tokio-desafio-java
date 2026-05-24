package br.com.tokio.transfer.scheduler.controller.mapper;

import br.com.tokio.transfer.scheduler.controller.dto.ScheduledTransferResponse;
import br.com.tokio.transfer.scheduler.model.ScheduledTransfer;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTransferControllerMapper {

    public ScheduledTransferResponse toResponse(ScheduledTransfer scheduledTransfer) {
        return new ScheduledTransferResponse(
                scheduledTransfer.getId(),
                scheduledTransfer.getSourceAccount(),
                scheduledTransfer.getDestinationAccount(),
                scheduledTransfer.getAmount(),
                scheduledTransfer.getFee(),
                scheduledTransfer.getAmount().add(scheduledTransfer.getFee()),
                scheduledTransfer.getTransferDate(),
                scheduledTransfer.getSchedulingDate()
        );
    }
}
