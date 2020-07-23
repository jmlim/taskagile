package io.jmlim.taskagile.web.apis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterationApiControllerTests {
    @Autowired
    private MockMvc mvc;

    @Test
    public void register_blankPayload_shouldFailAndReturn400() throws Exception {
        mvc.perform(post("/api/registrations"))
                .andExpect(status().is(400));
    }
}
