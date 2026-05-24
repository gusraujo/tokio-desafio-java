package br.com.tokio.transfer.scheduler.repository;

import br.com.tokio.transfer.scheduler.entity.ScheduledTransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledTransferRepository extends JpaRepository<ScheduledTransferEntity, Long> {
}
