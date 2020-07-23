package io.jmlim.taskagile.domain.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * DomainEventPublisher의 기본 구현은
 * 스프링 애플리케이션 이벤트 기반임
 */
@Component
@RequiredArgsConstructor
public class DefaultDomainEventPublisher implements DomainEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * publish a domain event
     *
     * @param event
     */
    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
