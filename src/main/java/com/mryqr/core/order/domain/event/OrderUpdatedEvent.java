package com.mryqr.core.order.domain.event;

import com.mryqr.common.domain.event.DomainEvent;
import com.mryqr.common.domain.event.DomainEventType;
import com.mryqr.common.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class OrderUpdatedEvent extends DomainEvent {
    private String orderId;

    public OrderUpdatedEvent(DomainEventType type, String orderId, User user) {
        super(type, user);
        this.orderId = orderId;
    }
}
