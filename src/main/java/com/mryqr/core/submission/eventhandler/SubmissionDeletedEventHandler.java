package com.mryqr.core.submission.eventhandler;

import com.mryqr.core.common.domain.event.DomainEvent;
import com.mryqr.core.common.domain.event.DomainEventHandler;
import com.mryqr.core.common.utils.MryTaskRunner;
import com.mryqr.core.qr.domain.task.SyncSubmissionAwareAttributeValuesForQrTask;
import com.mryqr.core.submission.domain.event.SubmissionDeletedEvent;
import com.mryqr.core.submission.domain.task.CountSubmissionForAppTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.mryqr.core.common.domain.event.DomainEventType.SUBMISSION_DELETED;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionDeletedEventHandler implements DomainEventHandler {
    private final SyncSubmissionAwareAttributeValuesForQrTask syncSubmissionAwareAttributesTask;
    private final CountSubmissionForAppTask countSubmissionForAppTask;

    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == SUBMISSION_DELETED;
    }

    @Override
    public void handle(DomainEvent domainEvent) {
        SubmissionDeletedEvent event = (SubmissionDeletedEvent) domainEvent;
        MryTaskRunner.run(() -> syncSubmissionAwareAttributesTask.run(event.getQrId(), event.getPageId()));
        MryTaskRunner.run(() -> countSubmissionForAppTask.run(event.getAppId(), event.getArTenantId()));
    }
}
