package io.jmlim.taskagile.domain.application.impl;

import io.jmlim.taskagile.domain.application.commands.RegistrationCommand;
import io.jmlim.taskagile.domain.common.event.DomainEventPublisher;
import io.jmlim.taskagile.domain.common.mail.MailManager;
import io.jmlim.taskagile.domain.common.mail.MessageVariable;
import io.jmlim.taskagile.domain.model.user.EmailAddressExistsException;
import io.jmlim.taskagile.domain.model.user.RegistrationException;
import io.jmlim.taskagile.domain.model.user.RegistrationManagement;
import io.jmlim.taskagile.domain.model.user.User;
import io.jmlim.taskagile.domain.model.user.events.UserRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;

class UserServiceImplTests {

    private RegistrationManagement registrationManagement;
    private DomainEventPublisher domainEventPublisher;
    private MailManager mailManager;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        registrationManagement = mock(RegistrationManagement.class);
        domainEventPublisher = mock(DomainEventPublisher.class);
        mailManager = mock(MailManager.class);
        userService = new UserServiceImpl(registrationManagement, domainEventPublisher, mailManager);
    }

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