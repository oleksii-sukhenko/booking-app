package mate.academy.booking.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.booking.dto.user.UserDataResponseDto;
import mate.academy.booking.dto.user.UserLoginRequestDto;
import mate.academy.booking.dto.user.UserRegisterRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void setUp(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/data.sql")
            );
        }
    }

    @Test
    @DisplayName("Register new user")
    void register_ValidRequest_ReturnsUserResponseDto() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto()
                .setEmail("user1@mail.com")
                .setFirstName("First")
                .setLastName("Last")
                .setPassword("password")
                .setRepeatPassword("password");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/auth/registration")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDataResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                UserDataResponseDto.class
        );

        assertEquals(requestDto.getEmail(), actual.email());
        assertEquals(requestDto.getFirstName(), actual.firstName());
    }

    @Test
    @DisplayName("Login with valid credentials")
    void login_ValidCredentials_ReturnToken() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto()
                .setEmail("user@mail.com")
                .setPassword("12345678");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assertThat(responseContent).contains("token");
    }
}
