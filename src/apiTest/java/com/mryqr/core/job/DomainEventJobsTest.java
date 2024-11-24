package com.mryqr.core.job;

import com.mryqr.BaseApiTest;
import com.mryqr.common.notification.publish.RedisNotificationDomainEventSender;
import com.mryqr.common.webhook.publish.RedisWebhookEventSender;
import com.mryqr.core.app.domain.App;
import com.mryqr.core.common.domain.AggregateRoot;
import com.mryqr.core.common.domain.event.DomainEvent;
import com.mryqr.core.common.domain.event.DomainEventJobs;
import com.mryqr.core.common.domain.event.publish.RedisDomainEventSender;
import com.mryqr.core.common.domain.user.Role;
import com.mryqr.core.common.domain.user.User;
import com.mryqr.core.common.properties.MryRedisProperties;
import com.mryqr.core.group.domain.Group;
import com.mryqr.core.member.domain.Member;
import com.mryqr.core.plate.domain.Plate;
import com.mryqr.core.qr.domain.QR;
import com.mryqr.core.qr.domain.QrCreatedEvent;
import com.mryqr.core.tenant.domain.Tenant;
import com.mryqr.utils.RandomTestFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.mryqr.core.common.domain.user.User.NOUSER;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

@Execution(SAME_THREAD)
public class DomainEventJobsTest extends BaseApiTest {
    @Autowired
    private DomainEventJobs domainEventJobs;

    @Autowired
    private RedisDomainEventSender redisDomainEventSender;

    @Autowired
    private RedisWebhookEventSender redisWebhookEventSender;

    @Autowired
    private RedisNotificationDomainEventSender redisNotificationDomainEventSender;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MryRedisProperties mryRedisProperties;

    @Test
    public void should_remove_old_domain_events_from_mongo() {
        QrCreatedEvent event = new QrCreatedEvent(QR.newQrId(), Plate.newPlateId(), Group.newGroupId(), App.newAppId(), NOUSER);
        ReflectionTestUtils.setField(event, "raisedAt", now().minus(300, DAYS));
        publishingDomainEventDao.stage(List.of(event));

        List<DomainEvent> dbEvents = publishingDomainEventDao.byIds(List.of(event.getId()));
        assertFalse(dbEvents.isEmpty());

        domainEventJobs.removeOldPublishingDomainEventsFromMongo(100);

        List<DomainEvent> updatedEvents = publishingDomainEventDao.byIds(List.of(event.getId()));
        assertTrue(updatedEvents.isEmpty());
    }

    //todo: 为removeOldConsumingDomainEventsFromMongo补充测试

    @Test
    @Disabled("not stable")
    public void should_remove_old_domain_events_from_redis() {
        String tenantId = Tenant.newTenantId();
        String qrId1 = QR.newQrId();
        AggregateRoot ar1 = new AggregateRoot(qrId1, User.humanUser(Member.newMemberId(), RandomTestFixture.rMemberName(), tenantId, Role.TENANT_ADMIN)) {
        };
        QrCreatedEvent event1 = new QrCreatedEvent(qrId1, Plate.newPlateId(), Group.newGroupId(), App.newAppId(), NOUSER);
        event1.setArInfo(ar1);

        String qrId2 = QR.newQrId();
        AggregateRoot ar2 = new AggregateRoot(qrId2, User.humanUser(Member.newMemberId(), RandomTestFixture.rMemberName(), tenantId, Role.TENANT_ADMIN)) {
        };
        QrCreatedEvent event2 = new QrCreatedEvent(qrId2, Plate.newPlateId(), Group.newGroupId(), App.newAppId(), NOUSER);
        event2.setArInfo(ar2);

        redisDomainEventSender.send(event1);
        redisDomainEventSender.send(event2);

        StreamInfo.XInfoStream info = stringRedisTemplate.opsForStream().info(mryRedisProperties.domainEventStreamForTenant(ar1.getTenantId()));
        assertTrue(info.streamLength() >= 2);

        domainEventJobs.removeOldDomainEventsFromRedis(1, false);
        StreamInfo.XInfoStream updatedInfo = stringRedisTemplate.opsForStream().info(mryRedisProperties.domainEventStreamForTenant(ar1.getTenantId()));
        assertEquals(1, updatedInfo.streamLength());
    }

    @Test
    public void should_remove_old_webhook_events_from_redis() {
        QrCreatedEvent event1 = new QrCreatedEvent(QR.newQrId(), Plate.newPlateId(), Group.newGroupId(), App.newAppId(), NOUSER);
        QrCreatedEvent event2 = new QrCreatedEvent(QR.newQrId(), Plate.newPlateId(), Group.newGroupId(), App.newAppId(), NOUSER);

        redisWebhookEventSender.send(event1);
        redisWebhookEventSender.send(event2);

        StreamInfo.XInfoStream info = stringRedisTemplate.opsForStream().info(mryRedisProperties.getWebhookStream());
        assertTrue(info.streamLength() >= 2);

        domainEventJobs.removeOldWebhookEventsFromRedis(1, false);
        StreamInfo.XInfoStream updatedInfo = stringRedisTemplate.opsForStream().info(mryRedisProperties.getWebhookStream());
        assertEquals(1, updatedInfo.streamLength());
    }

    @Test
    public void should_remove_old_notification_events_from_redis() {
        QrCreatedEvent event1 = new QrCreatedEvent(QR.newQrId(), Plate.newPlateId(), Group.newGroupId(), App.newAppId(), NOUSER);
        QrCreatedEvent event2 = new QrCreatedEvent(QR.newQrId(), Plate.newPlateId(), Group.newGroupId(), App.newAppId(), NOUSER);

        redisNotificationDomainEventSender.send(event1);
        redisNotificationDomainEventSender.send(event2);

        StreamInfo.XInfoStream info = stringRedisTemplate.opsForStream().info(mryRedisProperties.getNotificationStream());
        assertTrue(info.streamLength() >= 2);

        domainEventJobs.removeOldNotificationEventsFromRedis(1, false);
        StreamInfo.XInfoStream updatedInfo = stringRedisTemplate.opsForStream().info(mryRedisProperties.getNotificationStream());
        assertEquals(1, updatedInfo.streamLength());
    }

}
