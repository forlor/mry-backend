package com.mryqr.common.domain.event.consume;

public interface DomainEventHandler<T> {

    default boolean isIdempotent() {
        return false;
    }

    default int priority() {
        return 0;
    }

    void handle(ConsumingDomainEvent<T> consumingDomainEvent);
}
