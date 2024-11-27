package com.mryqr.core.tenant.domain.task;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public final class TenantRecentActiveTimeHolder {
    private final StringRedisTemplate stringRedisTemplate;

    public void recordRecentActiveTime(String tenantId) {
        stringRedisTemplate.opsForValue().set(getKey(tenantId), Instant.now().toString());
    }

    public Optional<Instant> getRecentActiveTime(String tenantId) {
        String timeString = stringRedisTemplate.opsForValue().get(getKey(tenantId));
        return Optional.ofNullable(timeString).map(Instant::parse);
    }

    private String getKey(String tenantId) {
        return "TenantRecentActiveTime:" + tenantId;
    }
}
