package com.mryqr.core.tenant.domain.task;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class TenantRecentActiveTimeHolder {
  private static final Map<String, Instant> holders = new ConcurrentHashMap<>();

  public static void recordRecentActiveTime(String tenantId) {
    holders.put(tenantId, Instant.now());
  }

  public static Optional<Instant> getRecentActiveTime(String tenantId) {
    return Optional.ofNullable(holders.get(tenantId));
  }
}
