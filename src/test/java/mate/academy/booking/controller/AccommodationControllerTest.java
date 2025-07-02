package mate.academy.booking.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import java.util.stream.Collectors;
import javax.sql.DataSource;
import mate.academy.booking.dto.accommodation.AccommodationResponseDto;
import mate.academy.booking.dto.accommodation.AccommodationResponseDtoWithoutAddressDto;
import mate.academy.booking.dto.accommodation.AmenityResponseDto;
import mate.academy.booking.dto.accommodation.CreateAccommodationRequestDto;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccommodationControllerTest {
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
    @DisplayName("Create accommodation")
    void createAccommodation_ValidRequestDto_Success() throws Exception {
        CreateAccommodationRequestDto requestDto
                = TestUtil.createAccommodationRequestDto();
        AccommodationResponseDto expected = TestUtil.getNewAccommodationDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/accommodation")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        AccommodationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AccommodationResponseDto.class
        );
        assertTrue(
                actual.amenities().containsAll(expected.amenities())
                        && expected.amenities().containsAll(actual.amenities())
        );
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id", "amenities"));
    }

    @Test
    @WithMockUser
    @DisplayName("Find all accommodations")
    void findAll_GivenAccommodations_ShouldReturnAllAccommodations() throws Exception {
        List<AccommodationResponseDto> expected = TestUtil.getAccommodationDtos().toList();

        MvcResult result = mockMvc.perform(get("/accommodation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = root.get("content");

        List<AccommodationResponseDto> actual = objectMapper.readValue(
                contentNode.traverse(),
                new TypeReference<>() {
                }
        );

        assertEquals(expected.size(), actual.size());
        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("amenities", "dailyRate")
                .containsExactlyInAnyOrderElementsOf(expected);
        for (int i = 0; i < expected.size(); i++) {
            AccommodationResponseDto expectedDto = expected.get(i);
            AccommodationResponseDto actualDto = actual.get(i);
            assertEquals(0, expectedDto.dailyRate().compareTo(actualDto.dailyRate()));
            assertEquals(expectedDto.amenities().size(), actualDto.amenities().size());
            assertTrue(actualDto.amenities().containsAll(expectedDto.amenities()));
        }
    }

    @Test
    @WithMockUser
    @DisplayName("Get accommodation by its ID")
    void findById_ValidId_ShouldReturnAccommodation() throws Exception {
        Long accommodationId = 1L;
        AccommodationResponseDto expected = TestUtil.getAccommodationDtos().toList().getFirst();

        MvcResult result = mockMvc.perform(get("/accommodation/{id}", accommodationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AccommodationResponseDto.class
        );

        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "dailyRate", "amenities"));
        assertEquals(0, expected.dailyRate().compareTo(actual.dailyRate()));
        assertEquals(expected.amenities().size(), actual.amenities().size());
        assertTrue(actual.amenities().containsAll(expected.amenities()));
    }

    @Test
    @WithMockUser
    @DisplayName("Get accommodations by address")
    @Sql(scripts = "classpath:database/data.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAvailableByAddressId_ValidAddress_ShouldReturnAvailableAccommodationsByItsAddress()
            throws Exception {
        Long addressId = 1L;
        List<AccommodationResponseDtoWithoutAddressDto> expected =
                TestUtil.getAccommodationResponseDtoWithoutAddressDto(addressId).getContent();

        MvcResult result = mockMvc.perform(get(
                        "/accommodation/by-address/{addressId}", addressId
                ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = root.get("content");

        List<AccommodationResponseDtoWithoutAddressDto> actual = objectMapper.readValue(
                contentNode.traverse(),
                new TypeReference<>() {
                }
        );

        assertEquals(expected.size(), actual.size());
        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("amenities", "dailyRate")
                .containsExactlyInAnyOrderElementsOf(expected);
        for (int i = 0; i < expected.size(); i++) {
            AccommodationResponseDtoWithoutAddressDto expectedDto = expected.get(i);
            AccommodationResponseDtoWithoutAddressDto actualDto = actual.get(i);
            assertEquals(0, expectedDto.dailyRate().compareTo(actualDto.dailyRate()));
            assertEquals(expectedDto.amenities().size(), actualDto.amenities().size());
            assertTrue(actualDto.amenities().containsAll(expectedDto.amenities()));
        }
    }

    @Test
    @WithMockUser
    @DisplayName("Get accommodations by amenity IDs")
    @Sql(scripts = "classpath:database/data.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAvailableByAmenityIds_ValidIds_ShouldReturnAccommodationsWithAmenities()
            throws Exception {
        List<Long> amenityIds = List.of(1L);

        List<AccommodationResponseDto> expected = TestUtil.getAccommodationDtos()
                .getContent().stream()
                .filter(dto -> dto.amenities().stream()
                        .map(AmenityResponseDto::id)
                        .collect(Collectors.toSet())
                        .containsAll(amenityIds))
                .toList();

        String amenityIdsParam = amenityIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        MvcResult result = mockMvc.perform(get(
                        "/accommodation/by-amenities"
                )
                        .param("amenityIds", amenityIdsParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = root.get("content");

        List<AccommodationResponseDto> actual = objectMapper.readValue(
                contentNode.traverse(),
                new TypeReference<>() {
                }
        );

        assertEquals(expected.size(), actual.size());
        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("dailyRate", "amenities")
                .containsExactlyInAnyOrderElementsOf(expected);
        for (int i = 0; i < expected.size(); i++) {
            AccommodationResponseDto expectedDto = expected.get(i);
            AccommodationResponseDto actualDto = actual.get(i);
            assertEquals(0, expectedDto.dailyRate().compareTo(actualDto.dailyRate()));
            assertEquals(expectedDto.amenities().size(), actualDto.amenities().size());
            assertTrue(actualDto.amenities().containsAll(expectedDto.amenities()));
        }
    }
}
