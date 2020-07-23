package io.jmlim.taskagile.domain.common.event;

public interface DomainEventPublisher {

    /**
     * publish a domain event
     *
     * @param event
     */
    void publish(DomainEvent event);

}
