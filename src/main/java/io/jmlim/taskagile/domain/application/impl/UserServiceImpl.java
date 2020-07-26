package io.jmlim.taskagile.domain.application.impl;

import io.jmlim.taskagile.domain.application.UserService;
import io.jmlim.taskagile.domain.application.commands.RegistrationCommand;
import io.jmlim.taskagile.domain.common.event.DomainEventPublisher;
import io.jmlim.taskagile.domain.common.mail.MailManager;
import io.jmlim.taskagile.domain.common.mail.MessageVariable;
import io.jmlim.taskagile.domain.model.user.*;
import io.jmlim.taskagile.domain.model.user.events.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final RegistrationManagement registrationManagement;
    private final DomainEventPublisher domainEventPublisher;
    private final MailManager mailManager;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException("No user found");
        }
        User user;
        if (username.contains("@")) {
            user = userRepository.findByEmailAddress(username)
                    .orElseThrow(() -> new UsernameNotFoundException("No user found by `" + username + "`"));
        } else {
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("No user found by `" + username + "`"));
        }
        return new SimpleUser(user);
    }

    /**
     * Register a new user with username, email address, and password
     *
     * @param command instance of RegistrationCommand
     * @throws RegistrationException when registration failed. Possible reasons are:
     *                               1) Username already exists
     *                               2) Email address already exists.
     */
    @Override
    public void register(RegistrationCommand command) throws RegistrationException {

        Assert.notNull(command, "Parameter `command` must not be null");
        User newUser = registrationManagement.register(command.getUsername(), command.getEmailAddress(), command.getPassword());

        this.sendWelcomeMessage(newUser);
        domainEventPublisher.publish(new UserRegisteredEvent(newUser));
    }

    private void sendWelcomeMessage(User user) {
        mailManager.send(
                user.getEmailAddress(),
                "Welcome to TaskAgile",
                "welcome.ftl",
                MessageVariable.from("user", user)
        );
    }
}
