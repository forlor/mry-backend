package com.mryqr.common.domain.event.consume;


import com.mryqr.common.properties.MryRedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

import static com.mryqr.common.utils.MryConstants.REDIS_DOMAIN_EVENT_CONSUMER_GROUP;
import static org.springframework.data.redis.connection.stream.Consumer.from;
import static org.springframework.data.redis.connection.stream.ReadOffset.lastConsumed;
import static org.springframework.data.redis.connection.stream.StreamOffset.create;

@Slf4j
@Profile("!ci")
@Configuration
@DependsOn("redisStreamInitializer")
@ConditionalOnProperty(value = "mry.redis.domainEventStreamEnabled", havingValue = "true")
@RequiredArgsConstructor
public class RedisDomainEventConsumeConfiguration {
    private final MryRedisProperties mryRedisProperties;
    private final DomainEventListener domainEventListener;

    @Qualifier("consumeDomainEventTaskExecutor")
    private final TaskExecutor consumeDomainEventTaskExecutor;

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> domainEventContainer(RedisConnectionFactory factory) {
        var options = StreamMessageListenerContainerOptions
                .builder()
                .batchSize(20)
                .executor(consumeDomainEventTaskExecutor)
                .targetType(String.class)
                .errorHandler(new MryRedisErrorHandler())
                .build();

        var container = StreamMessageListenerContainer.create(factory, options);

        mryRedisProperties.allDomainEventStreams().forEach(stream -> {
            container.receiveAutoAck(
                    from(REDIS_DOMAIN_EVENT_CONSUMER_GROUP, "DomainEventRedisStreamConsumer-" + stream),
                    create(stream, lastConsumed()),
                    domainEventListener);
        });

        container.start();
        log.info("Start consuming domain events from redis stream.");
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

