package br.com.tokio.transfer.scheduler.repository.mapper;

import br.com.tokio.transfer.scheduler.repository.entity.ScheduledTransferEntity;
import br.com.tokio.transfer.scheduler.model.ScheduledTransfer;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTransferMapper {

    public ScheduledTransferEntity toEntity(ScheduledTransfer scheduledTransfer) {
        return new ScheduledTransferEntity(
                scheduledTransfer.getId(),
                scheduledTransfer.getSourceAccount(),
                scheduledTransfer.getDestinationAccount(),
                scheduledTransfer.getAmount(),
                scheduledTransfer.getFee(),
                scheduledTransfer.getTransferDate(),
                scheduledTransfer.getSchedulingDate()
        );
    }

    public ScheduledTransfer toDomain(ScheduledTransferEntity entity) {
        return new ScheduledTransfer(
                entity.getId(),
                entity.getSourceAccount(),
                entity.getDestinationAccount(),
                entity.getAmount(),
                entity.getFee(),
                entity.getTransferDate(),
                entity.getSchedulingDate()
        );
    }
}
