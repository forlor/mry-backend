package com.mryqr.common.notification.publish;

import com.mryqr.common.event.DomainEvent;
import com.mryqr.common.profile.NonBuildProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NonBuildProfile
@RequiredArgsConstructor
public class AsynchronousNotificationEventPublisher implements NotificationEventPublisher {
    private final RedisNotificationDomainEventSender redisNotificationDomainEventSender;

    @Override
    public void publish(DomainEvent domainEvent) {
        redisNotificationDomainEventSender.send(domainEvent);
    }
}
