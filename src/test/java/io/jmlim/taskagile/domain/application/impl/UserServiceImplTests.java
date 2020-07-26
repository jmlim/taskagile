package io.jmlim.taskagile.domain.application.impl;

import io.jmlim.taskagile.domain.application.commands.RegistrationCommand;
import io.jmlim.taskagile.domain.common.event.DomainEventPublisher;
import io.jmlim.taskagile.domain.common.mail.MailManager;
import io.jmlim.taskagile.domain.common.mail.MessageVariable;
import io.jmlim.taskagile.domain.model.user.*;
import io.jmlim.taskagile.domain.model.user.events.UserRegisteredEvent;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;

class UserServiceImplTests {

    private RegistrationManagement registrationManagement;
    private DomainEventPublisher domainEventPublisher;
    private MailManager mailManager;
    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        registrationManagement = mock(RegistrationManagement.class);
        domainEventPublisher = mock(DomainEventPublisher.class);
        mailManager = mock(MailManager.class);
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(registrationManagement, domainEventPublisher, mailManager, userRepository);
    }

    //-------------------------------------------
    // Method loadUserByUsername()
    //-------------------------------------------

    @Test
    @DisplayName("유저 이름이 비었을 시 Fail")
    void loadUserByUsername_emptyUsername_shouldFail() {
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(""));

        // verify()는 해당 구문이 호출되었는지 체크한다.
        // 단순 호출뿐 아니라 횟수나 타임아웃 시간까지 지정해서 체크해 볼 수 있다.
        // 해당 함수가 빈 값으로 호출된 적이 없는지 검증. (never() 은 호출되지 않음을 검증)
        // https://twofootdog.github.io/Spring-Spring-MVC%EC%97%90%EC%84%9C-JUnit-%ED%99%9C%EC%9A%A9%ED%95%98%EA%B8%B03(mockito%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-Service,-DAO-%ED%85%8C%EC%8A%A4%ED%8A%B8)/

        verify(userRepository, never()).findByUsername("");
        verify(userRepository, never()).findByEmailAddress("");
    }

    @Test
    @DisplayName("사용자가 존재하지만 Fail")
    void loadUserByUsername_notExistUsername_shouldFail() {
        String existUsername = "임정묵";

        // userRepository.findByUsername 이 notExistUsername을 받을 경우 리턴값이 empty 로 반환되는 것을 가정
        when(userRepository.findByUsername(existUsername)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(existUsername));

        verify(userRepository).findByUsername(existUsername);
        verify(userRepository, never()).findByEmailAddress(existUsername);
    }

    @Test
    @DisplayName("사용자 존재하며 로드 성공 시")
    void loadUserByUsername_existUsername_shouldSucceed() throws IllegalAccessException {
        String existUsername = "임정묵";
        User foundUser = User.create(existUsername, "hackerljm@gmail.com", "EncryptedPassword!");
        foundUser.updateName("Test", "User");

        FieldUtils.writeField(foundUser, "id", 1L, true);
        when(userRepository.findByUsername(existUsername)).thenReturn(Optional.of(foundUser));

        UserDetails userDetails = userService.loadUserByUsername(existUsername);

        verify(userRepository).findByUsername(existUsername);
        verify(userRepository, never()).findByEmailAddress(existUsername);

        assertNotNull(userDetails);
        assertEquals(existUsername, userDetails.getUsername());
        assertTrue(userDetails instanceof SimpleUser);
    }


    //-------------------------------------------
    // Method register()
    //-------------------------------------------

    /**
     * 아래 예제는 throw 된 예외로 전달 된 메시지를 확인하고 싶지 않거나 확인 할 필요가 없는 간단한 경우에 사용한다.
     */
    @Test
    @DisplayName("null 객체 등록 시에 fail 처리")
    void register_nullCommand_shouldFail() throws RegistrationException {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class
                , () -> userService.register(null));
        String message = exception.getMessage();
        assertEquals("Parameter `command` must not be null", message);
    }

    @Test
    @DisplayName("존재하는 사용자 이름 등록 시 Fail")
    void register_existingUsername_shouldFail() throws RegistrationException {
        String username = "림정묵";
        String emailAddress = "hackerljm@gmail.com";
        String password = "MyPassword!";

        // registerException 을 상속받는 EmailAddressExistsException 상황 재현
        doThrow(EmailAddressExistsException.class)
                .when(registrationManagement)
                .register(username, emailAddress, password);

        RegistrationCommand command = new RegistrationCommand(username, emailAddress, password);
        assertThrows(RegistrationException.class, () -> userService.register(command));
    }

    @Test
    @DisplayName("등록 성공 시")
    void register_validCommand_shouldSucceed() throws RegistrationException {
        String username = "림정묵";
        String emailAddress = "hackerljm@gmail.com";
        String password = "MyPassword!";
        User newUser = User.create(username, emailAddress, password);

        when(registrationManagement.register(username, emailAddress, password))
                .thenReturn(newUser);

        RegistrationCommand command = new RegistrationCommand(username, emailAddress, password);

        userService.register(command);

        // verify 를 이용하여 mock 객체에 대한 원하는 메소드가 특정 조건으로 실행되었는지 검증 할 수 있다.
        // refEq 참고 https://zorba91.tistory.com/235
        // https://stackoverrun.com/ko/q/6914083
        verify(mailManager).send(
                refEq(emailAddress),
                refEq("Welcome to TaskAgile"),
                refEq("welcome.ftl"),
                refEq(MessageVariable.from("user", newUser)));
        verify(domainEventPublisher).publish(new UserRegisteredEvent(newUser));
    }
}