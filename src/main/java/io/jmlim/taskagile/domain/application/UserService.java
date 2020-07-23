package io.jmlim.taskagile.domain.application;

import io.jmlim.taskagile.domain.application.commands.RegistrationCommand;
import io.jmlim.taskagile.domain.model.user.RegistrationException;

public interface UserService {
    /**
     * Register a new user with username, email address, and password
     *
     * @param command instance of RegistrationCommand
     * @throws RegistrationException when registration failed. Possible reasons are:
     *                               1) Username already exists
     *                               2) Email address already exists.
     */
    void register(RegistrationCommand command) throws RegistrationException;
}
