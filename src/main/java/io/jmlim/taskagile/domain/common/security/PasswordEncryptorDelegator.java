package io.jmlim.taskagile.domain.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncryptorDelegator implements PasswordEncryptor {

    private final PasswordEncoder passwordEncoder;
    /**
     * Encrypt a raw password
     *
     * @param rawPassword
     */
    @Override
    public String encrypt(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
