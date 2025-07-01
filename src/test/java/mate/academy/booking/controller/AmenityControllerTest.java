package mate.academy.booking.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import mate.academy.booking.dto.accommodation.AmenityResponseDto;
import mate.academy.booking.dto.accommodation.CreateAmenityRequestDto;
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
public class AmenityControllerTest {
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
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("Create amenity")
    void createAmenity_ValidRequest_Success() throws Exception {
        CreateAmenityRequestDto requestDto = TestUtil.createAmenityRequestDto();
        AmenityResponseDto expected = TestUtil.getNewAmenityDto();

        MvcResult result = mockMvc.perform(post("/amenity")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        AmenityResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AmenityResponseDto.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser
    @DisplayName("Get all amenities")
    void getAllAmenities_GivenAmenities_ShouldReturnAmenities() throws Exception {
        List<AmenityResponseDto> expected = TestUtil.getAmenityDtos().toList();

        MvcResult result = mockMvc.perform(get("/amenity")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = root.get("content");

        List<AmenityResponseDto> actual = objectMapper.readValue(
                contentNode.traverse(),
                new TypeReference<>() {
                }
        );

        assertEquals(expected.size(), actual.size());
        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @WithMockUser
    @DisplayName("Get amenity by id")
    void getAmenityById_ValidId_ReturnsAmenity() throws Exception {
        AmenityResponseDto expected = TestUtil.getAmenityDtos().stream().toList().getLast();
        MvcResult result = mockMvc
                .perform(get("/amenity/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AmenityResponseDto actual = objectMapper
                .readValue(
                        result.getResponse().getContentAsString(),
                        AmenityResponseDto.class
                );
        assertEquals(expected, actual);
    }
}
