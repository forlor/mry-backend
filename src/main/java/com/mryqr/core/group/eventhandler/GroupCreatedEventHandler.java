package com.mryqr.core.group.eventhandler;

import com.mryqr.core.common.domain.event.DomainEvent;
import com.mryqr.core.common.domain.event.DomainEventHandler;
import com.mryqr.core.common.utils.MryTaskRunner;
import com.mryqr.core.group.domain.event.GroupCreatedEvent;
import com.mryqr.core.group.domain.task.CountGroupForAppTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.mryqr.core.common.domain.event.DomainEventType.GROUP_CREATED;

@Component
@RequiredArgsConstructor
public class GroupCreatedEventHandler implements DomainEventHandler {
    private final CountGroupForAppTask countGroupForAppTask;

    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == GROUP_CREATED;
    }

    @Override
    public void handle(DomainEvent domainEvent) {
        GroupCreatedEvent event = (GroupCreatedEvent) domainEvent;
        MryTaskRunner.run(() -> countGroupForAppTask.run(event.getAppId(), event.getArTenantId()));
    }
}
