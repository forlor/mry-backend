package com.mryqr.core.common.domain.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Comparator.comparingInt;

@Deprecated
@Slf4j
@Component
public class DomainEventConsumer {
    private final List<DomainEventHandler> handlers;

    public DomainEventConsumer(List<DomainEventHandler> handlers) {
        this.handlers = handlers;
        this.handlers.sort(comparingInt(DomainEventHandler::priority));
    }

    //所有能处理事件的handler依次处理，全部处理成功记录消费成功，否则记录为消费失败；
    //消费失败后，兜底机制将重新发送事件，重新发送最多不超过3次
    public void consume(DomainEvent domainEvent) {
        log.info("Start consume domain event[{}:{}:{}:{}].",
                domainEvent.getType(), domainEvent.getId(), domainEvent.getArId(), domainEvent.getArTenantId());

//        boolean hasError = false;
        for (DomainEventHandler handler : handlers) {
            try {
                if (handler.canHandle(domainEvent)) {
                    handler.handle(domainEvent);
                }
            } catch (Throwable t) {
//                hasError = true;
                log.error("Error while handle domain event[{}:{}] by [{}].",
                        domainEvent.getType(), domainEvent.getId(), handler.getClass().getSimpleName(), t);
            }
        }

//        if (taskRunner.isHasError()) {
//            hasError = true;
//        }

//        if (hasError) {
//            domainEventDao.failConsume(domainEvent);
//        } else {
//            domainEventDao.successConsume(domainEvent);
//        }
    }

}
