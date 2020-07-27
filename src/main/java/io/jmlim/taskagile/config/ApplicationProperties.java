package io.jmlim.taskagile.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "app")
@Validated
public class ApplicationProperties {

    @Setter
    @Email
    @NotBlank
    private String mailFrom;
}
