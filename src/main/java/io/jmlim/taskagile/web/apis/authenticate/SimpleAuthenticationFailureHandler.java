package io.jmlim.taskagile.web.apis.authenticate;

import io.jmlim.taskagile.utils.JsonUtils;
import io.jmlim.taskagile.web.results.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SimpleAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * Called when an authentication attempt fails.
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        response.setStatus(HttpStatus.BAD_REQUEST.value());

        ApiResult failure;
        if(exception instanceof BadCredentialsException) {
            failure = ApiResult.message("Invalid credentials");
        } else if(exception instanceof InsufficientAuthenticationException) {
            failure = ApiResult.message("Invalid authentication request");
        } else {
            failure = ApiResult.message("Authentication failure");
        }
        JsonUtils.write(response.getWriter(), failure);
    }
}