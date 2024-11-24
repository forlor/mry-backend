package com.mryqr.core.assignmentplan.eventhandler;

import com.mryqr.core.assignment.domain.task.RemoveAllAssignmentsUnderAssignmentPlanTask;
import com.mryqr.core.assignmentplan.domain.event.AssignmentPlanDeletedEvent;
import com.mryqr.core.common.domain.event.consume.AbstractDomainEventHandler;
import com.mryqr.core.common.utils.MryTaskRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignmentPlanDeletedEventHandler extends AbstractDomainEventHandler<AssignmentPlanDeletedEvent> {
    private final RemoveAllAssignmentsUnderAssignmentPlanTask removeAllAssignmentsUnderAssignmentPlanTask;

    @Override
    protected void doHandle(AssignmentPlanDeletedEvent event) {
        MryTaskRunner.run(() -> removeAllAssignmentsUnderAssignmentPlanTask.run(event.getAssignmentPlanId()));
    }

    @Override
    public boolean isIdempotent() {
        return true;
    }
}
