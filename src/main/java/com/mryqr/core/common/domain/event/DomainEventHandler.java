package com.mryqr.core.common.domain.event;

@Deprecated
public interface DomainEventHandler {

    boolean canHandle(DomainEvent domainEvent);

    void handle(DomainEvent domainEvent);

    default int priority() {
        return 0;//越小优先级越高
    }

}
