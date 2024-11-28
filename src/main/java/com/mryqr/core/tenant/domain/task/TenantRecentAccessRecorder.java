package com.mryqr.core.tenant.domain.task;

import com.mryqr.core.tenant.domain.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.mryqr.core.tenant.domain.Tenant.Fields.recentAccessTime;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
@RequiredArgsConstructor
public class TenantRecentAccessRecorder {
    private final MongoTemplate mongoTemplate;

    public void record(String tenantId) {
        Update update = new Update();
        update.set(recentAccessTime, Instant.now());
        mongoTemplate.updateFirst(Query.query(where("_id").is(tenantId)), update, Tenant.class);
    }

}
