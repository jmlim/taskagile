package io.jmlim.taskagile.web.apis;

import io.jmlim.taskagile.domain.application.UserService;
import io.jmlim.taskagile.domain.model.user.EmailAddressExistsException;
import io.jmlim.taskagile.domain.model.user.RegistrationException;
import io.jmlim.taskagile.domain.model.user.UsernameException;
import io.jmlim.taskagile.web.payload.RegistrationPayload;
import io.jmlim.taskagile.web.results.ApiResult;
import io.jmlim.taskagile.web.results.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class RegistrationApiController {
    private final UserService service;

    @PostMapping("/api/registrations")
    public ResponseEntity<ApiResult> register(@Valid @RequestBody RegistrationPayload payload) {
        try {
            service.register(payload.toCommand());
            return Result.created();
        } catch (RegistrationException e) {
            String errorMessage = "Registration failed";
            if (e instanceof UsernameException) {
                errorMessage = "Username already exists";
            } else if (e instanceof EmailAddressExistsException) {
                errorMessage = "Email address already exists";
            }
            return Result.failure(errorMessage);
        }
    }
}
