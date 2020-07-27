package io.jmlim.taskagile.domain.common.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class DefaultMailManager implements MailManager {

    private final String mailFrom;
    private final Mailer mailer;
    private final Configuration configuration;

    public DefaultMailManager(@Value("${app.mail-from}") String mailFrom,
                              Mailer mailer,
                              Configuration configuration) {
        this.mailFrom = mailFrom;
        this.mailer = mailer;
        this.configuration = configuration;
    }

    /**
     * Send a message to a recipient
     *
     * @param emailAddress the recipient's email address
     * @param subject      the subject key of the email
     * @param template     the template file name of the email
     * @param variables    message variables in the template file
     */
    @Override
    public void send(String emailAddress, String subject, String template, MessageVariable... variables) {

        Assert.hasText(emailAddress, "Parameter `emailAddress` must not be blank");
        Assert.hasText(subject, "Parameter `subject` must not be blank");
        Assert.hasText(template, "Parameter `template` must not be blank");

        String messagebody = createMessageBody(template, variables);
        Message message = new SimpleMessage(emailAddress, subject, messagebody, mailFrom);
        mailer.send(message);
    }

    private String createMessageBody(String templateName, MessageVariable... variables) {
        try {
            Template template = configuration.getTemplate(templateName);
            Map<String, Object> model = new HashMap<>();
            if (Objects.nonNull(variables)) {
                for (MessageVariable variable : variables) {
                    model.put(variable.getKey(), variable.getValue());
                }
            }
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (TemplateException | IOException e) {
            log.error("Failed to create message body from template `" + templateName + "`", e);
            return null;
        }
    }
}
