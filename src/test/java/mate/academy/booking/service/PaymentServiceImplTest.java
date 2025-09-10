package mate.academy.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import mate.academy.booking.dto.payment.PaymentResponseDto;
import mate.academy.booking.exception.EntityNotFoundException;
import mate.academy.booking.mapper.PaymentMapper;
import mate.academy.booking.model.Accommodation;
import mate.academy.booking.model.Booking;
import mate.academy.booking.model.Payment;
import mate.academy.booking.model.User;
import mate.academy.booking.repository.booking.BookingRepository;
import mate.academy.booking.repository.payment.PaymentRepository;
import mate.academy.booking.service.notification.TelegramNotificationService;
import mate.academy.booking.service.payment.PaymentServiceImpl;
import mate.academy.booking.service.stripe.StripeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private StripeService stripeService;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private TelegramNotificationService notificationService;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Booking booking;
    private Payment payment;
    private PaymentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        user = new User()
                .setId(1L)
                .setEmail("user@example>com");

        Accommodation accommodation = new Accommodation()
                .setId(1L)
                .setDailyRate(BigDecimal.TEN);

        booking = new Booking()
                .setId(1L)
                .setUser(user)
                .setAccommodation(accommodation)
                .setCheckInDate(LocalDate.now())
                .setCheckOutDate(LocalDate.now().plusDays(2));

        payment = new Payment()
                .setId(1L)
                .setBooking(booking)
                .setAmountToPay(BigDecimal.valueOf(20))
                .setStatus(Payment.Status.PENDING)
                .setSessionId("session1")
                .setSessionUrl("http://session.url")
                .setCreatedAt(LocalDateTime.now());

        responseDto = new PaymentResponseDto(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getAmountToPay(),
                payment.getSessionUrl(),
                payment.getSessionId(),
                payment.getStatus()
        );
    }

    @Test
    void createPaymentSession_ValidBooking_ReturnsDto() throws StripeException {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(paymentRepository.findFirstByBookingIdAndStatusOrderByCreatedAtDesc(
                booking.getId(), Payment.Status.PENDING)
        ).thenReturn(Optional.empty());

        Session session = new Session();
        session.setId("session1");
        session.setUrl("http://session.url");

        doReturn(session).when(stripeService).createStripeSession(any(), any());

        when(paymentRepository.save(any())).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(responseDto);

        PaymentResponseDto result = paymentService.createPaymentSession(booking.getId(), user);

        assertEquals(responseDto, result);
        verify(paymentRepository).save(any());
    }

    @Test
    void createPaymentSession_BookingNotFound_ShouldThrowException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> paymentService.createPaymentSession(-1L, user));
    }

    @Test
    void createPaymentSession_AccessDenied_ShouldThrowException() {
        User otherUser = new User().setId(user.getId() + 1);
        booking.setUser(otherUser);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class,
                () -> paymentService.createPaymentSession(booking.getId(), user));
    }

    @Test
    void handleSuccess_ValidSession_UpdatesStatusAndSendNotification() {
        when(paymentRepository.findBySessionId(payment.getSessionId()))
                .thenReturn(Optional.of(payment));

        String result = paymentService.handleSuccess(payment.getSessionId());

        assertEquals("Payment successful", result);
        assertEquals(Payment.Status.PAID, payment.getStatus());
        verify(paymentRepository).save(payment);
        verify(notificationService).notifyAdmin("Booking ID 1 has been paid");
    }

    @Test
    void handleCancel_ValidSession_UpdatesStatusAndSendNotification() {
        when(paymentRepository.findBySessionId(payment.getSessionId()))
                .thenReturn(Optional.of(payment));

        String result = paymentService.handleCancel(payment.getSessionId());

        assertEquals("Payment was cancelled or paused.", result);
        assertEquals(Payment.Status.CANCELED, payment.getStatus());
        verify(paymentRepository).save(payment);
        verify(notificationService).notifyAdmin("Booking ID 1 payment has been canceled");
    }

    @Test
    void expiredOldPayments_ShouldUpdateExpiredPayments() {
        LocalDateTime oldTime = LocalDateTime.now().minusMinutes(31);
        payment.setCreatedAt(oldTime);

        when(paymentRepository.findAllByStatusAndCreatedAtBefore(eq(Payment.Status.PENDING), any()))
                .thenReturn(List.of(payment));

        paymentService.expireOldPayments();

        assertEquals(Payment.Status.EXPIRED, payment.getStatus());
        verify(paymentRepository).saveAll(List.of(payment));
    }
}
