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
import mate.academy.booking.dto.accommodation.AddressResponseDto;
import mate.academy.booking.dto.accommodation.CreateAddressRequestDto;
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
public class AddressControllerTest {
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
    @DisplayName("Create address")
    void createAddress_ValidRequest_Success() throws Exception {
        CreateAddressRequestDto requestDto = TestUtil.createAddressRequestDto();
        AddressResponseDto expected = TestUtil.getNewAddressDto();

        MvcResult result = mockMvc.perform(post("/location")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        AddressResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AddressResponseDto.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser
    @DisplayName("Get all locations")
    void getAllAddresses_GivenAmenities_ShouldReturnAmenities() throws Exception {
        List<AddressResponseDto> expected = TestUtil.getAddressDtos().toList();

        MvcResult result = mockMvc.perform(get("/location")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = root.get("content");

        List<AddressResponseDto> actual = objectMapper.readValue(
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
    @DisplayName("Get address by id")
    void getAddressById_ValidId_ReturnsAmenity() throws Exception {
        AddressResponseDto expected = TestUtil.getAddressDtos().stream().toList().getFirst();
        MvcResult result = mockMvc
                .perform(get("/location/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AddressResponseDto actual = objectMapper
                .readValue(
                        result.getResponse().getContentAsString(),
                        AddressResponseDto.class
                );
        assertEquals(expected, actual);
    }
}
