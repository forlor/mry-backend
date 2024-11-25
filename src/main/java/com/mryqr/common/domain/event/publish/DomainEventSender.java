package com.mryqr.common.domain.event.publish;

import com.mryqr.common.domain.event.DomainEvent;

import java.util.concurrent.CompletableFuture;

public interface DomainEventSender {
    CompletableFuture<String> send(DomainEvent domainEvent);
}
