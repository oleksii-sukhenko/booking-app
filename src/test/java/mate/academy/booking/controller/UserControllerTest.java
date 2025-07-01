package mate.academy.booking.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import mate.academy.booking.dto.user.UpdateUserRoleRequestDto;
import mate.academy.booking.dto.user.UserDataResponseDto;
import mate.academy.booking.dto.user.UserRoleResponseDto;
import mate.academy.booking.util.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {
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
    @WithMockUser(username = "user@mail.com", roles = {"CUSTOMER"})
    @DisplayName("Get profile info for authenticated user")
    void getProfileInfo_AuthenticatedUser_Success() throws Exception {
        UserDataResponseDto expected = TestUtil.getUserProfile();

        MvcResult result = mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDataResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserDataResponseDto.class
        );

        assertNotNull(actual);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("Get all users - as manager")
    void getAllUsers_AsManager_ReturnsUsers() throws Exception {
        MvcResult result = mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = root.get("content");

        List<UserRoleResponseDto> users = objectMapper.readValue(
                contentNode.traverse(),
                new TypeReference<>() {
                }
        );

        assertThat(users).isNotEmpty();
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("Update user role - as manager")
    void updateUserRole_AsManager_Success() throws Exception {
        UpdateUserRoleRequestDto requestDto = new UpdateUserRoleRequestDto();
        requestDto.setRoleId(1L);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/users/{id}/role", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        UserRoleResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserRoleResponseDto.class
        );

        assertNotNull(actual);
        assertEquals(1L, actual.userId());
        assertEquals("user@mail.com", actual.email());
        assertThat(actual.roleIds()).contains(1L);
    }
}
