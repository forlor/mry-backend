package com.mryqr.support;

import com.mryqr.common.profile.BuildProfile;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@BuildProfile
@Configuration
public class SyncConsumeDomainEventInterceptorConfigurer implements WebMvcConfigurer {
  private final SyncConsumeDomainEventHandlerInterceptor syncConsumeDomainEventHandlerInterceptor;

  public SyncConsumeDomainEventInterceptorConfigurer(SyncConsumeDomainEventHandlerInterceptor interceptor) {
    this.syncConsumeDomainEventHandlerInterceptor = interceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(syncConsumeDomainEventHandlerInterceptor);
  }
}
