package com.mryqr.common.domain.event.publish;

import com.mryqr.common.domain.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.mryqr.common.domain.event.publish.DomainEventPublishStatus.*;
import static com.mryqr.common.domain.event.publish.PublishingDomainEvent.Fields.*;
import static com.mryqr.common.utils.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

// When publishing domain event, firstly we stage(save) the event in DB in the same transaction that handles business logic
@Slf4j
@Component
@RequiredArgsConstructor
public class PublishingDomainEventDao {
    private final MongoTemplate mongoTemplate;

    public void stage(List<DomainEvent> events) {
        requireNonNull(events, "Domain events must not be null.");
        List<PublishingDomainEvent> publishingDomainEvents = events.stream().map(PublishingDomainEvent::new).toList();
        mongoTemplate.insertAll(publishingDomainEvents);
    }

    public List<DomainEvent> stagedEvents(String startId, int limit) {
        requireNonBlank(startId, "Start ID must not be blank.");

        Query query = query(where(status).in(CREATED, PUBLISH_FAILED)
                .and("_id").gt(startId)
                .and(publishedCount).lt(3))
                .with(by(ASC, raisedAt))
                .limit(limit);
        return mongoTemplate.find(query, PublishingDomainEvent.class).stream().map(PublishingDomainEvent::getEvent).toList();
    }

    public List<DomainEvent> byIds(List<String> ids) {
        requireNonNull(ids, "Domain event IDs must not be null.");

        Query query = query(where("_id").in(ids)).with(by(ASC, raisedAt));
        List<PublishingDomainEvent> events = mongoTemplate.find(query, PublishingDomainEvent.class);
        return events.stream().map(PublishingDomainEvent::getEvent).toList();
    }


    public void successPublish(String eventId) {
        requireNonBlank(eventId, "Domain event ID must not be blank.");
        Query query = Query.query(where("_id").is(eventId));
        Update update = new Update();
        update.set(status, PUBLISH_SUCCEED.name()).inc(publishedCount);
        mongoTemplate.updateFirst(query, update, PublishingDomainEvent.class);
    }

    public void failPublish(String eventId) {
        requireNonBlank(eventId, "Domain event ID must not be blank.");
        Query query = Query.query(where("_id").is(eventId));
        Update update = new Update();
        update.set(status, PUBLISH_FAILED.name()).inc(publishedCount);
        mongoTemplate.updateFirst(query, update, PublishingDomainEvent.class);
    }

}
