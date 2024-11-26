package com.mryqr.common.event.publish;

import java.util.List;

import com.mryqr.common.event.DomainEvent;

public interface PublishingDomainEventDao {
  void stage(List<DomainEvent> events);

  List<DomainEvent> stagedEvents(String startId, int limit);

  List<DomainEvent> byIds(List<String> ids);

  void successPublish(String eventId);

  void failPublish(String eventId);
}
