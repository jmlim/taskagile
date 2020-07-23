package io.jmlim.taskagile.domain.model.user.events;

import io.jmlim.taskagile.domain.common.event.DomainEvent;
import io.jmlim.taskagile.domain.model.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class UserRegisteredEvent extends DomainEvent {
    private User user;

    public UserRegisteredEvent(User user) {
        super(user);
        Assert.notNull(user, "Parameter `user` must not be null");
        this.user = user;
    }
}
