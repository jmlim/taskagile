package io.jmlim.taskagile.web.apis;

import io.jmlim.taskagile.domain.application.UserService;
import io.jmlim.taskagile.domain.model.user.EmailAddressExistsException;
import io.jmlim.taskagile.domain.model.user.UsernameException;
import io.jmlim.taskagile.utils.JsonUtils;
import io.jmlim.taskagile.web.payload.RegistrationPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RegistrationApiController.class)
public class RegisterationApiControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService serviceMock;

    @Test
    @DisplayName("빈 페이로드로 요청 시 400 error")
    void register_blankPayload_shouldFailAndReturn400() throws Exception {
        mvc.perform(post("/api/registrations"))
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("이미 존재하는 사용자 이름 등록 시도 시 400 error")
    void register_existedUsename_shouldFailAndReturn400() throws Exception {
        RegistrationPayload payload = new RegistrationPayload();
        payload.setUsername("림정묵");
        payload.setEmailAddress("hackerljm@gmail.com");
        payload.setPassword("MyPassword!");

        // UserService의 register 메소드에 커멘드 아규먼트가 들어올 시 UsernameException 발생
        // 결국엔 무조건 UsernameException 을 발생시키게 함.
        doThrow(UsernameException.class)
                .when(serviceMock)
                .register(payload.toCommand());

        mvc.perform(
                post("/api/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(payload)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message")
                        .value("Username already exists"));
    }

    @Test
    @DisplayName("이미 존재하는 이메일 주소 등록 시 400 error")
    void register_existedEmailAddress_shouldFailAndReturn400() throws Exception {
        RegistrationPayload payload = new RegistrationPayload();
        payload.setUsername("림정묵");
        payload.setEmailAddress("hackerljm@gmail.com");
        payload.setPassword("MyPassword!");

        doThrow(EmailAddressExistsException.class)
                .when(serviceMock)
                .register(payload.toCommand());

        mvc.perform(
                post("/api/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(payload)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("Email address already exists"));
    }

    @Test
    @DisplayName("유효한 페이로드를 등록 요청 시 Succeed")
    void register_validPayload_shouldSucceedAndReturn201() throws Exception {
        RegistrationPayload payload = new RegistrationPayload();
        payload.setUsername("림정묵");
        payload.setEmailAddress("hackerljm@gmail.com");
        payload.setPassword("MyPassword!");

        doNothing().when(serviceMock)
                .register(payload.toCommand());

        mvc.perform
                (post("/api/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(payload)))
                .andExpect(status().is(201));

    }
}
