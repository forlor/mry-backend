package com.mryqr.common.webhook.publish;

import com.mryqr.common.domain.event.DomainEvent;

public interface MryWebhookEventPublisher {
    void publish(DomainEvent domainEvent);
}
