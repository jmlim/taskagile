package io.jmlim.taskagile.domain.common.mail;

import freemarker.template.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class DefaultMailManagerTest {


    @TestConfiguration
    static class DefaultMessageCreatorConfiguration {
        @Bean
        public FreeMarkerConfigurationFactoryBean getFreemarkerConfiguration() {
            FreeMarkerConfigurationFactoryBean factoryBean = new FreeMarkerConfigurationFactoryBean();
            factoryBean.setTemplateLoaderPath("/mail-templates/");
            return factoryBean;
        }
    }

    @Autowired
    private Configuration configuration;
    private Mailer mailer;
    private DefaultMailManager defaultMailManager;

    @BeforeEach
    void setUp() {
        mailer = mock(Mailer.class);
        defaultMailManager = new DefaultMailManager("noreply@gmail.com", mailer, configuration);
    }

    @Test
    void send_nullEmailAddress_shouldFail() {
        assertThrows(IllegalArgumentException.class,
                () -> defaultMailManager.send(null, "Test subject", "test.ftl"));
    }

    @Test
    public void send_emptyEmailAddress_shouldFail() {
        assertThrows(IllegalArgumentException.class,
                () -> defaultMailManager.send("", "Test subject", "test.ftl"));
    }

    @Test
    public void send_nullSubject_shouldFail() {
        assertThrows(IllegalArgumentException.class,
                () -> defaultMailManager.send("test@taskagile.com", null, "test.ftl"));
    }

    @Test
    public void send_emptySubject_shouldFail() {
        assertThrows(IllegalArgumentException.class,
                () -> defaultMailManager.send("test@taskagile.com", "", "test.ftl"));
    }

    @Test
    public void send_nullTemplateName_shouldFail() {
        assertThrows(IllegalArgumentException.class,
                () -> defaultMailManager.send("test@taskagile.com", "Test subject", null));
    }

    @Test
    public void send_emptyTemplateName_shouldFail() {
        assertThrows(IllegalArgumentException.class,
                () -> defaultMailManager.send("test@taskagile.com", "Test subject", ""));
    }

    @Test
    public void send_validParameters_shouldSucceed() {
        String to = "hackerljm@gmail.com";
        String subject = "Test subject";
        String templateName = "test.ftl";

        defaultMailManager.send(to, subject, templateName, MessageVariable.from("name", "test"));
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);

        verify(mailer).send(messageArgumentCaptor.capture());

        Message messageSent = messageArgumentCaptor.getValue();

        assertEquals(to, messageSent.getTo());
        assertEquals(subject, messageSent.getSubject());
        assertEquals("noreply@gmail.com", messageSent.getFrom());
        assertEquals("Hello, test", messageSent.getBody());
    }
}