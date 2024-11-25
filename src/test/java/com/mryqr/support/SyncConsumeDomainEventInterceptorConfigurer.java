package com.mryqr.support;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("ci")
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
