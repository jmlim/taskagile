package io.jmlim.taskagile.domain.model.user;

import io.jmlim.taskagile.domain.common.security.PasswordEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

class RegistrationManagementTests {

    private UserRepository userRepository;
    private PasswordEncryptor passwordEncryptor;
    private RegistrationManagement registrationManagement;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncryptor = mock(PasswordEncryptor.class);
        registrationManagement = new RegistrationManagement(userRepository, passwordEncryptor);
    }

    @Test
    @DisplayName("존재하는 유저 네임 등록 시 fail")
    void register_existedUsername_shouldFail() {
        String username = "존재하는 림정묵";
        String emailAddress = "hackerljm@gmail.com";
        String password = "MyPassword!";

        // 빈 사용자 객체를 반환하여 기존 사용자가 있는것을 나타냄.
        // userRepository.findByUsername의 존재하여 리턴값은 Optional.of(new User()) 이 된다고 가정..
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(new User()));

        assertThrows(UsernameException.class, () ->
                registrationManagement.register(username, emailAddress, password));
    }

    @Test
    @DisplayName("존재하는 이메일 등록 시 fail")
    void register_existedEmailAddress_shouldFail() {
        String username = "림정묵";
        String emailAddress = "exsit_hackerljm@gmail.com";
        String password = "MyPassword!";

        // emailAddress 가 존재하여 리턴값은 Optional.of(new User()) 이 된다고 가정..
        when(userRepository.findByEmailAddress(emailAddress))
                .thenReturn(Optional.of(new User()));

        assertThrows(EmailAddressExistsException.class, () ->
                registrationManagement.register(username, emailAddress, password));
    }

    @Test
    @DisplayName("대문자 전자 우편 주소 등록에 성공하고 소문자가 되도록")
    void register_uppercaseEmailAddress_shouldSucceedAndBecomeLowercase() throws RegistrationException {
        String username = "임정묵";
        String emailAddress = "HackerLjm@Gmail.com";
        String password = "MyPassword!";

        registrationManagement.register(username, emailAddress, password);
        User userToSave = User.create(username, emailAddress.toLowerCase(), password);

        verify(userRepository).save(userToSave);
    }

    @Test
    @DisplayName("유저 등록 성공 시")
    void register_newUser_shouldSucceed() throws RegistrationException {
        String username = "림정묵";
        String emailAddress = "hackerljm@gmail.com";
        String password = "MyPassword!";
        String encryptPassword = "EncryptedPassword";

        User newUser = User.create(username, emailAddress.toLowerCase(), encryptPassword);
        // 등록하려는 emailAddress 가 존재하지 않음.
        when(userRepository.findByEmailAddress(emailAddress))
                .thenReturn(Optional.empty());
        // 등록하려는 유저 이름이 존재하지 않음
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        doNothing().when(userRepository).save(newUser);
        when(passwordEncryptor.encrypt(password)).thenReturn("EncryptedPassword");

        User savedUser = registrationManagement.register(username, emailAddress, password);

        // userRepository 에 있는 메소드가 특정 순서대로 호출되는지 검증하기 위해 모키토의 InOrder API 활용..
        InOrder inOrder = inOrder(userRepository);
        inOrder.verify(userRepository).findByUsername(username);
        inOrder.verify(userRepository).findByEmailAddress(emailAddress);
        inOrder.verify(userRepository).save(newUser);

        verify(passwordEncryptor).encrypt(password);

        System.out.println(encryptPassword);
        System.out.println(savedUser.getPassword());

        assertEquals("Saved User's password should be encrypted", encryptPassword, savedUser.getPassword());
    }
}