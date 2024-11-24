package com.mryqr.core.qr.eventhandler;

import com.mryqr.core.common.domain.event.DomainEvent;
import com.mryqr.core.common.domain.event.DomainEventHandler;
import com.mryqr.core.common.utils.MryTaskRunner;
import com.mryqr.core.plate.domain.Plate;
import com.mryqr.core.plate.domain.PlateRepository;
import com.mryqr.core.plate.domain.task.UnbindPlateFromQrTask;
import com.mryqr.core.platebatch.domain.task.CountUsedPlatesForPlateBatchTask;
import com.mryqr.core.qr.domain.event.QrDeletedEvent;
import com.mryqr.core.qr.domain.task.CountQrForAppTask;
import com.mryqr.core.submission.domain.task.CountSubmissionForAppTask;
import com.mryqr.core.submission.domain.task.RemoveAllSubmissionsForQrTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.mryqr.core.common.domain.event.DomainEventType.QR_DELETED;

@Slf4j
@Component
@RequiredArgsConstructor
public class QrDeletedEventHandler implements DomainEventHandler {
    private final CountQrForAppTask countQrForAppTask;
    private final PlateRepository plateRepository;
    private final CountUsedPlatesForPlateBatchTask countUsedPlatesForPlateBatchTask;
    private final RemoveAllSubmissionsForQrTask removeAllSubmissionsForQrTask;
    private final UnbindPlateFromQrTask unbindPlateFromQrTask;
    private final CountSubmissionForAppTask countSubmissionForAppTask;

    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == QR_DELETED;
    }

    @Override
    public void handle(DomainEvent domainEvent) {
        QrDeletedEvent event = (QrDeletedEvent) domainEvent;
        MryTaskRunner.run(() -> unbindPlateFromQrTask.run(event.getQrId()));
        MryTaskRunner.run(() -> removeAllSubmissionsForQrTask.run(event.getQrId()));
        MryTaskRunner.run(() -> countSubmissionForAppTask.run(event.getAppId(), event.getArTenantId()));
        MryTaskRunner.run(() -> countQrForAppTask.run(event.getAppId(), event.getArTenantId()));
        plateRepository.byIdOptional(event.getPlateId())
                .filter(Plate::isBatched)
                .ifPresent(plate -> MryTaskRunner.run(() -> countUsedPlatesForPlateBatchTask.run(plate.getBatchId())));
    }

}
