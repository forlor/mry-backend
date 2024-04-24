package com.mryqr.common.event.consume;


import com.mryqr.core.common.properties.MryRedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.util.ErrorHandler;

import static com.mryqr.core.common.utils.MryConstants.REDIS_DOMAIN_EVENT_CONSUMER_GROUP;
import static java.time.Duration.ofMinutes;
import static java.time.Instant.now;
import static org.springframework.data.redis.connection.stream.Consumer.from;
import static org.springframework.data.redis.connection.stream.ReadOffset.lastConsumed;
import static org.springframework.data.redis.connection.stream.StreamOffset.create;

@Slf4j
@Profile("!ci")
@Configuration
@DependsOn("redisStreamInitializer")
@RequiredArgsConstructor
public class RedisEventContainerConfiguration {
    // 要求所有节点的部署在10分钟内完成，否则可能导致多个节点重复消费消息
    private static final LockConfiguration LOCK_CONFIGURATION = new LockConfiguration(now(), "domainEventsListenerContainer", ofMinutes(60), ofMinutes(10));
    private final MryRedisProperties mryRedisProperties;
    private final DomainEventListener domainEventListener;

    private final LockingTaskExecutor lockingTaskExecutor;

    @Qualifier("consumeDomainEventTaskExecutor")
    private final TaskExecutor consumeDomainEventTaskExecutor;

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> domainEventContainer(RedisConnectionFactory factory) throws Throwable {
        log.info("Start create redis stream listener container for consuming domain events.");

        var options = StreamMessageListenerContainerOptions
                .builder()
                .batchSize(20)
                .executor(consumeDomainEventTaskExecutor)
                .targetType(String.class)
                .errorHandler(new MryRedisErrorHandler())
                .build();

        var container = StreamMessageListenerContainer.create(factory, options);

        LockingTaskExecutor.TaskResult<Integer> taskResult = lockingTaskExecutor.executeWithLock(() -> {
            mryRedisProperties.allDomainEventStreams().forEach(stream -> {
                container.receiveAutoAck(
                        from(REDIS_DOMAIN_EVENT_CONSUMER_GROUP, "DomainEventRedisStreamConsumer-" + stream),
                        create(stream, lastConsumed()),
                        domainEventListener);
            });
            return 0;
        }, LOCK_CONFIGURATION);

        if (taskResult.wasExecuted()) {
            log.info("Start consuming domain events from redis stream.");
        } else {
            log.info("Skip consuming domain events from redis stream due to been locked.");
        }

        container.start();
        return container;
    }

    @Slf4j
    private static class MryRedisErrorHandler implements ErrorHandler {
        @Override
        public void handleError(Throwable t) {
            log.error(t.getMessage());
        }
    }

}

