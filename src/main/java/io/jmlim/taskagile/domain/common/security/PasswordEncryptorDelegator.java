package io.jmlim.taskagile.domain.common.security;

import org.springframework.stereotype.Component;

@Component
public class PasswordEncryptorDelegator implements PasswordEncryptor {

    /**
     * Encrypt a raw password
     *
     * @param rawPassword
     */
    @Override
    public String encrypt(String rawPassword) {
        // TODO: implement this
        return rawPassword;
    }
}
