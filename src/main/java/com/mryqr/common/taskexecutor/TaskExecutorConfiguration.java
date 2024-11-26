package com.mryqr.common.taskexecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableAsync
@Configuration
public class TaskExecutorConfiguration {

  @Bean
  @Primary
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(500);
    executor.initialize();
    executor.setThreadNamePrefix("mry-common-");
    return executor;
  }

  @Bean
  public TaskExecutor qrAccessCountTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(500);
    executor.initialize();
    executor.setThreadNamePrefix("mry-access-qr-");
    return executor;
  }

  @Bean
  public TaskExecutor consumeDomainEventTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(500);
    executor.initialize();
    executor.setThreadNamePrefix("mry-event-");
    return executor;
  }

  @Bean
  public TaskExecutor sendWebhookTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(500);
    executor.initialize();
    executor.setThreadNamePrefix("mry-webhook-");
    return executor;
  }

  @Bean
  public TaskExecutor sendNotificationTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(500);
    executor.initialize();
    executor.setThreadNamePrefix("mry-notify-");
    return executor;
  }

  @Bean
  public SchedulingTaskExecutor threadPoolTaskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(5);
    scheduler.setThreadNamePrefix("mry-scheduling-");
    return scheduler;
  }
}
