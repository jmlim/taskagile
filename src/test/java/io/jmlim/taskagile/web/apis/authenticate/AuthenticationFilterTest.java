package io.jmlim.taskagile.web.apis.authenticate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class AuthenticationFilterTest {

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("로그인 요청 바디가 없는 경우 ")
    void attemptAuthentication_emptyRequestBody_shouldFail() throws IOException, ServletException {
        System.out.println(authenticationManager);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/authentications");
        AuthenticationFilter filter = new AuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);

        assertThrows(InsufficientAuthenticationException.class,
                () -> filter.attemptAuthentication(request, new MockHttpServletResponse()));
    }

    @Test
    @DisplayName("유효하지 않은 Json 문자열 요청")
    void attemptAuthentication_invalidJsonStringRequestBody_shouldFail() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/authentications");
        request.setContent("username=testusername&password=TestPassword!".getBytes());

        AuthenticationFilter filter = new AuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);

        assertThrows(InsufficientAuthenticationException.class,
                () -> filter.attemptAuthentication(request, new MockHttpServletResponse()));
    }

    @Test
    @DisplayName("유효한 Json 문자열 요청 후 성공")
    void attemptAuthentication_validJsonStringRequestBody_shouldSucceed() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/authentications");
        request.setContent("{\"username\": \"testusername\", \"password\": \"TestPassword!\"}".getBytes());
        AuthenticationFilter filter = new AuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.attemptAuthentication(request, new MockHttpServletResponse());

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("testusername", "TestPassword!");

        verify(authenticationManager).authenticate(token);
    }
}