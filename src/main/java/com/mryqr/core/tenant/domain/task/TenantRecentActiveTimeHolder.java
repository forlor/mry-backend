package com.mryqr.core.tenant.domain.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class TenantRecentActiveTimeHolder {
    private final Map<String, Instant> holder = new ConcurrentHashMap<>();

    public void recordRecentActiveTime(String tenantId) {
        this.holder.put(tenantId, Instant.now());
    }

    public Optional<Instant> getRecentActiveTime(String tenantId) {
        return Optional.ofNullable(this.holder.get(tenantId));
    }
}
