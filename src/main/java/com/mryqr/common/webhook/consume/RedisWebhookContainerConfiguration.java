package com.mryqr.common.webhook.consume;

import static com.mryqr.common.utils.MryConstants.REDIS_WEBHOOK_CONSUMER_GROUP;
import static org.springframework.data.redis.connection.stream.Consumer.from;
import static org.springframework.data.redis.connection.stream.ReadOffset.lastConsumed;
import static org.springframework.data.redis.connection.stream.StreamOffset.create;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.IntStream;

import com.mryqr.common.profile.NonBuildProfile;
import com.mryqr.common.properties.MryRedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.util.ErrorHandler;

@Slf4j
@NonBuildProfile
@Configuration
@DependsOn("redisStreamInitializer")
@RequiredArgsConstructor
public class RedisWebhookContainerConfiguration {
  private final MryRedisProperties mryRedisProperties;
  private final WebhookEventListener webhookEventListener;

  @Qualifier("sendWebhookTaskExecutor")
  private final TaskExecutor sendWebhookTaskExecutor;

  @Bean
  public StreamMessageListenerContainer<String, ObjectRecord<String, String>> webhookEventContainer(RedisConnectionFactory factory) {
    var options = StreamMessageListenerContainerOptions
        .builder()
        .batchSize(10)
        .executor(sendWebhookTaskExecutor)
        .targetType(String.class)
        .errorHandler(new MryRedisErrorHandler())
        .build();

    var container = StreamMessageListenerContainer.create(factory, options);

    IntStream.range(1, 11).forEach(index -> {
      try {
        container.receiveAutoAck(
            from(REDIS_WEBHOOK_CONSUMER_GROUP, InetAddress.getLocalHost().getHostName() + "-" + index),
            create(mryRedisProperties.getWebhookStream(), lastConsumed()),
            webhookEventListener);
      } catch (UnknownHostException e) {
        throw new RuntimeException(e);
      }
    });

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

