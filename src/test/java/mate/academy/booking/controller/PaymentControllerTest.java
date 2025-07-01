package mate.academy.booking.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mate.academy.booking.dto.booking.BookingRequestDto;
import mate.academy.booking.dto.booking.BookingResponseDto;
import mate.academy.booking.dto.payment.PaymentResponseDto;
import mate.academy.booking.model.Booking;
import mate.academy.booking.model.Payment;
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
public class PaymentControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
    @DisplayName("Create payment session")
    void createPaymentSession_ValidBooking_ReturnsPayment() throws Exception {
        Long bookingId = 1L;

        MvcResult result = mockMvc.perform(post("/payments")
                        .param("bookingId", bookingId.toString()))
                .andExpect(status().isCreated())
                .andReturn();

        PaymentResponseDto payment = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                PaymentResponseDto.class
        );

        assertNotNull(payment);
        assertEquals(bookingId, payment.bookingId());
        assertEquals(Payment.Status.PENDING, payment.status());
        assertNotNull(payment.sessionId());
        assertNotNull(payment.sessionUrl());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = {"CUSTOMER"})
    @DisplayName("Create payment session - booking not found")
    void createPaymentSession_BookingNotFound_ReturnsNotFound() throws Exception {
        long nonExistentBookingId = 999L;

        mockMvc.perform(post("/payments")
                        .param("bookingId", Long.toString(nonExistentBookingId)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user2@mail.com", roles = {"CUSTOMER"})
    @DisplayName("Create payment session - forbidden for wrong user")
    void createPaymentSession_WrongUser_Forbidden() throws Exception {
        long bookingId = 1L;

        mockMvc.perform(post("/payments")
                        .param("bookingId", Long.toString(bookingId)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = {"CUSTOMER"})
    @DisplayName("Get all payments for user")
    void getUserPayments_ValidUser_ReturnsPayments() throws Exception {
        Long userId = 1L;

        Set<Long> bookingIdsOfUser = TestUtil.getBookingsForUser(userId).stream()
                .map(BookingResponseDto::id)
                .collect(Collectors.toSet());

        MvcResult result = mockMvc.perform(get("/payments/my"))
                .andExpect(status().isOk())
                .andReturn();

        List<PaymentResponseDto> payments = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );

        assertFalse(payments.isEmpty());

        assertThat(payments)
                .allSatisfy(payment ->
                        assertTrue(
                                bookingIdsOfUser.contains(payment.bookingId()),
                                "Payment with bookingId " + payment.bookingId() + " does not belong to user " + userId
                        )
                );
    }


    @Test
    @WithMockUser(username = "user@mail.com", roles = {"CUSTOMER"})
    @DisplayName("Handle payment success")
    void handleSuccess_ValidSessionId_ReturnsSuccessMessage() throws Exception {
        String sessionId = "session_def456";

        MvcResult result = mockMvc.perform(get("/payments/success")
                        .param("session_id", sessionId))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("Payment successful"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = {"CUSTOMER"})
    @DisplayName("Handle payment cancel")
    void handleCancel_ValidSessionId_ReturnsCancelMessage() throws Exception {
        String sessionId = "session_abc123";

        MvcResult result = mockMvc.perform(get("/payments/cancel")
                        .param("session_id", sessionId))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("Payment was cancelled"));
    }
}
