package com.mryqr.core.group.eventhandler;

import com.mryqr.core.common.domain.event.DomainEvent;
import com.mryqr.core.common.domain.event.DomainEventHandler;
import com.mryqr.core.common.utils.MryTaskRunner;
import com.mryqr.core.group.domain.event.GroupDeactivatedEvent;
import com.mryqr.core.qr.domain.task.SyncGroupActiveStatusToQrsTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.mryqr.core.common.domain.event.DomainEventType.GROUP_DEACTIVATED;

@Component
@RequiredArgsConstructor
public class GroupDeactivatedEventHandler implements DomainEventHandler {
    private final SyncGroupActiveStatusToQrsTask syncGroupActiveStatusToQrsTask;

    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == GROUP_DEACTIVATED;
    }

    @Override
    public void handle(DomainEvent domainEvent) {
        GroupDeactivatedEvent event = (GroupDeactivatedEvent) domainEvent;

        MryTaskRunner.run(() -> syncGroupActiveStatusToQrsTask.run(event.getGroupId()));
    }
}
