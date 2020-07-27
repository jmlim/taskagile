package io.jmlim.taskagile.infrastructure.mailer;

import io.jmlim.taskagile.domain.common.mail.SimpleMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AsyncMailerTest {

    private JavaMailSender javaMailSender;
    private AsyncMailer asyncMailer;

    @BeforeEach
    void setUp() {
        javaMailSender = mock(JavaMailSender.class);
        asyncMailer = new AsyncMailer(javaMailSender);
    }

    @Test
    @DisplayName("null 메세지 전달 후 Fail")
    void send_nullMessage_shouldFail() {
        assertThrows(IllegalArgumentException.class, () -> asyncMailer.send(null));
    }

    @Test
    void send_validMessage_shouldSucceed() {
        String from = "hackerljm@gmail.com";
        String to = "hackerljm@naver.com";
        String subject = "A test message";
        String body = "Username: test, Email Address: hackerljm@naver.com";

        SimpleMessage message = new SimpleMessage(to, subject, body, from);
        asyncMailer.send(message);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText("Username: test, Email Address: hackerljm@naver.com");
        verify(javaMailSender).send(simpleMailMessage);
    }
}