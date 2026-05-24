package br.com.tokio.transfer.scheduler.controller;

import br.com.tokio.transfer.scheduler.controller.dto.ScheduleTransferRequest;
import br.com.tokio.transfer.scheduler.controller.dto.ScheduledTransferResponse;
import br.com.tokio.transfer.scheduler.controller.mapper.ScheduledTransferControllerMapper;
import br.com.tokio.transfer.scheduler.model.ScheduledTransfer;
import br.com.tokio.transfer.scheduler.service.ScheduledTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/scheduled-transfers")
@RequiredArgsConstructor
public class ScheduledTransferController {

    private final ScheduledTransferService scheduledTransferService;
    private final ScheduledTransferControllerMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduledTransferResponse schedule(@Valid @RequestBody ScheduleTransferRequest request) {
        ScheduledTransfer scheduledTransfer = scheduledTransferService.schedule(
                request.getSourceAccount(),
                request.getDestinationAccount(),
                request.getAmount(),
                request.getTransferDate()
        );

        return mapper.toResponse(scheduledTransfer);
    }

    @GetMapping
    public List<ScheduledTransferResponse> findAll() {
        return scheduledTransferService.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
