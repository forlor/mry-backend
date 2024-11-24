package com.mryqr.core.app.eventhandler;

import com.mryqr.core.app.domain.event.AppGroupSyncEnabledEvent;
import com.mryqr.core.common.domain.event.DomainEvent;
import com.mryqr.core.common.domain.event.DomainEventHandler;
import com.mryqr.core.common.utils.MryTaskRunner;
import com.mryqr.core.group.domain.task.SyncAllDepartmentsToGroupTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.mryqr.core.common.domain.event.DomainEventType.APP_GROUP_SYNC_ENABLED;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupSyncEnabledEventHandler implements DomainEventHandler {
    private final SyncAllDepartmentsToGroupTask syncAllDepartmentsToGroupTask;

    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == APP_GROUP_SYNC_ENABLED;
    }

    @Override
    public void handle(DomainEvent domainEvent) {
        AppGroupSyncEnabledEvent theEvent = (AppGroupSyncEnabledEvent) domainEvent;
        MryTaskRunner.run(() -> syncAllDepartmentsToGroupTask.run(theEvent.getAppId()));
    }
}
