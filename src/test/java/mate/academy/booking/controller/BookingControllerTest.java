package mate.academy.booking.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;
import mate.academy.booking.dto.booking.BookingRequestDto;
import mate.academy.booking.dto.booking.BookingResponseDto;
import mate.academy.booking.model.Booking;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingControllerTest {
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
    @DisplayName("Create booking")
    void create_ValidRequest_ReturnsCreated() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate checkIn = today.plusDays(1);
        LocalDate checkOut = today.plusDays(2);

        BookingRequestDto requestDto = new BookingRequestDto()
                .setAccommodationId(1L)
                .setCheckInDate(checkIn)
                .setCheckOutDate(checkOut);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        BookingResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookingResponseDto.class
        );

        BookingResponseDto expected = new BookingResponseDto(
                null,
                checkIn,
                checkOut,
                requestDto.getAccommodationId(),
                1L,
                Booking.Status.PENDING
        );

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("Find booking by id")
    void findBookingById_ValidId_ReturnsBooking() throws Exception {
        BookingResponseDto expected = TestUtil.getBookingsForUser(1L).getContent().getFirst();

        MvcResult result = mockMvc.perform(get("/bookings/{id}", expected.id()))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookingResponseDto.class
        );

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("Find all bookings by status and user ID")
    @Sql(scripts = "/database/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByUserIdAndStatus_ValidParams_ReturnsFilteredBookings() throws Exception {
        List<BookingResponseDto> expected = TestUtil.getBookingsForUser(1L).stream()
                .filter(b -> b.status() == Booking.Status.CONFIRMED)
                .toList();

        MvcResult result = mockMvc.perform(get("/bookings")
                        .param("userId", "1")
                        .param("status", "CONFIRMED")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = root.get("content");

        List<BookingResponseDto> actual = objectMapper.readValue(
                contentNode.traverse(),
                new TypeReference<>() {
                }
        );

        assertThat(actual)
                .allSatisfy(dto -> {
                    assertEquals(Booking.Status.CONFIRMED, dto.status());
                    assertEquals(1L, dto.userId());
                });
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = {"CUSTOMER"})
    @DisplayName("Get bookings for authenticated user")
    @Sql(scripts = "classpath:database/data.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findForAuthenticatedUser_ReturnsOwnBookings() throws Exception {
        List<BookingResponseDto> expected = TestUtil.getBookingsForUser(1L).toList();

        MvcResult result = mockMvc.perform(get("/bookings/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = root.get("content");

        List<BookingResponseDto> actual = objectMapper.readValue(
                contentNode.traverse(),
                new TypeReference<>() {
                }
        );

        assertEquals(expected.size(), actual.size());
        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrderElementsOf(expected);
    }
}
