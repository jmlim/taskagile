package io.jmlim.taskagile.domain.model.user;

import io.jmlim.taskagile.domain.common.security.PasswordEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * User registeration domain service
 */
@Component
@RequiredArgsConstructor
public class RegistrationManagement {
    private final UserRepository userRepository;
    private final PasswordEncryptor passwordEncryptor;

    public User register(String username, String emailAddress, String password) throws RegistrationException {
        if (userRepository.findByUsername(username).isPresent()) throw new UsernameException();
        if (userRepository.findByEmailAddress(emailAddress.toLowerCase()).isPresent())
            throw new EmailAddressExistsException();

        String encrypt = passwordEncryptor.encrypt(password);
        User newUser = User.create(username, emailAddress.toLowerCase(), encrypt);
        userRepository.save(newUser);
        return newUser;
    }
}
