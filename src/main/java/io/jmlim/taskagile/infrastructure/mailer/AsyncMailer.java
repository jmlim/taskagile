package io.jmlim.taskagile.infrastructure.mailer;

import io.jmlim.taskagile.domain.common.mail.Mailer;
import io.jmlim.taskagile.domain.common.mail.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncMailer implements Mailer {

    private final JavaMailSender mailSender;


    /**
     * 보내는 메세지
     *
     * @param message
     */
    @Async
    @Override
    public void send(Message message) {
        Assert.notNull(message, "Parameter `message` must not be null");

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        if (StringUtils.isNotBlank(message.getFrom())) {
            mailMessage.setFrom(message.getFrom());
        }
        if (StringUtils.isNotBlank(message.getSubject())) {
            mailMessage.setSubject(message.getSubject());
        }
        if (StringUtils.isNotEmpty(message.getBody())) {
            mailMessage.setText(message.getBody());
        }

        if (Objects.nonNull(message.getTo())) {
            mailMessage.setTo(message.getTo());
        }

        mailSender.send(mailMessage);
    }
}
