package com.mryqr.common.domain.event.consume;

import com.mryqr.common.domain.event.DomainEvent;
import com.mryqr.common.tracing.MryTracingService;
import com.mryqr.common.utils.MryObjectMapper;
import io.micrometer.tracing.ScopedSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventListener implements StreamListener<String, ObjectRecord<String, String>> {
    private final MryObjectMapper mryObjectMapper;
    private final DomainEventConsumer<DomainEvent> domainEventConsumer;
    private final MryTracingService mryTracingService;

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        ScopedSpan scopedSpan = mryTracingService.startNewSpan("domain-event-listener");

        String jsonString = message.getValue();
        DomainEvent domainEvent = mryObjectMapper.readValue(jsonString, DomainEvent.class);
        try {
            domainEventConsumer.consume(new ConsumingDomainEvent<>(domainEvent.getId(), domainEvent.getType().name(), domainEvent));
        } catch (Throwable t) {
            log.error("Failed to listen domain event[{}:{}].", domainEvent.getType(), domainEvent.getId(), t);
        }

        scopedSpan.end();
    }
}
