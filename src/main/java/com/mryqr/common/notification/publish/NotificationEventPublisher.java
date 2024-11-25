package com.mryqr.common.notification.publish;

import com.mryqr.common.domain.event.DomainEvent;

public interface NotificationEventPublisher {
    void publish(DomainEvent domainEvent);
}
