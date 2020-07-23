package io.jmlim.taskagile.domain.model.user;

import java.util.Optional;

/**
 * User repository interface
 */
public interface UserRepository {

    /**
     * Find user by a name
     *
     * @param username
     * @return
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by an email address
     *
     * @param emailAddress
     * @return
     */
    Optional<User> findByEmailAddress(String emailAddress);

    /**
     * Save a new user or an existing user
     *
     * @param user
     */
    void save(User user);
}
