package io.jmlim.taskagile.domain.common.event;

import org.springframework.context.ApplicationEvent;

public abstract class DomainEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DomainEvent(Object source) {
        super(source);
    }

    /**
     * 이벤트가 발생한 timestamp  반환
     *
     * @return
     */
    public long occuredAt() {
        //
        //기본 구현의 타임 스탬프를 반환
        return getTimestamp();
    }
}
