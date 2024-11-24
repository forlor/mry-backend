package com.mryqr.testing.support.event.sync;

import com.mryqr.core.common.domain.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

// Synchronously consume domain events without publishing them to the messaging middleware.
// It has the following advantages:
// 1. No need for messaging middle to set up, not even an embedded one
// 2. Avoid waiting for some criteria to met in order to do testing verification

@Aspect
@Component
@Profile("ci")
@RequiredArgsConstructor
public class SyncConsumeDomainEventAspect {

    @After("execution(* com.mryqr.core.common.domain.event.publish.PublishingDomainEventDao.stage(..))")
    public void storeDomainEventIds(JoinPoint joinPoint) {
        if (joinPoint.getArgs()[0] instanceof List<?> events) {
            events.forEach((Object event) -> {
                ThreadLocalDomainEventIdHolder.addEvents((List<DomainEvent>) events);
            });
        }
    }
}
