package mate.academy.booking.service.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mate.academy.booking.dto.payment.PaymentResponseDto;
import mate.academy.booking.mapper.PaymentMapper;
import mate.academy.booking.model.Booking;
import mate.academy.booking.model.Payment;
import mate.academy.booking.model.Payment.Status;
import mate.academy.booking.model.User;
import mate.academy.booking.repository.booking.BookingRepository;
import mate.academy.booking.repository.payment.PaymentRepository;
import mate.academy.booking.service.notification.TelegramNotificationService;
import mate.academy.booking.service.stripe.StripeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private static final int THRESHOLD = 30;
    private static final String PAYMENT_SUCCESS
            = "Booking ID %s has been paid";
    private static final String PAYMENT_CANCEL
            = "Booking ID %s payment has been canceled";

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;
    private final PaymentMapper paymentMapper;
    private final TelegramNotificationService notificationService;

    @Override
    public List<PaymentResponseDto> getPaymentSession(Long userId) {
        return paymentRepository.findByBookingUserId(userId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentResponseDto createPaymentSession(Long bookingId, User user) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to pay for this booking");
        }

        Optional<Payment> existing = paymentRepository
                .findFirstByBookingIdAndStatusOrderByCreatedAtDesc(
                bookingId, Status.PENDING
        );

        if (existing.isPresent()) {
            return paymentMapper.toDto(existing.get());
        }

        BigDecimal amountToPay = calculateAmountToPay(booking);
        Payment payment = createPaymentWithSession(booking, amountToPay);

        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional
    public Payment createPaymentWithSession(Booking booking, BigDecimal amountToPay) {
        Payment payment = new Payment()
                .setBooking(booking)
                .setAmountToPay(amountToPay)
                .setStatus(Status.PENDING)
                .setCreatedAt(LocalDateTime.now());

        try {
            Session session = stripeService.createStripeSession(booking, amountToPay);
            payment.setSessionId(session.getId());
            payment.setSessionUrl(session.getUrl());
            return paymentRepository.save(payment);
        } catch (StripeException e) {
            payment.setStatus(Status.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Failed to create Stripe session", e);
        }
    }

    @Override
    public String handleSuccess(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        if (payment.getStatus() == Status.PENDING) {
            payment.setStatus(Status.PAID);
            paymentRepository.save(payment);
        }

        Booking booking = payment.getBooking();

        notificationService.notifyAdmin(
                String.format(PAYMENT_SUCCESS, booking.getId())
        );

        return "Payment successful";
    }

    @Override
    public String handleCancel(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        if (payment.getStatus() == Status.PENDING) {
            payment.setStatus(Status.CANCELED);
            paymentRepository.save(payment);
        }

        Booking booking = payment.getBooking();

        notificationService.notifyAdmin(
                String.format(PAYMENT_CANCEL, booking.getId())
        );

        return "Payment was cancelled or paused.";
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireOldPayments() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(THRESHOLD);
        List<Payment> oldPayments = paymentRepository
                .findAllByStatusAndCreatedAtBefore(Status.PENDING, threshold);

        for (Payment payment : oldPayments) {
            payment.setStatus(Status.EXPIRED);
        }

        if (!oldPayments.isEmpty()) {
            paymentRepository.saveAll(oldPayments);
            log.info("Expired {} old payments", oldPayments.size());
        }
    }

    private BigDecimal calculateAmountToPay(Booking booking) {
        long numberOfDays = ChronoUnit.DAYS.between(
                booking.getCheckInDate(), booking.getCheckOutDate()
        );

        if (numberOfDays <= 0) {
            throw new IllegalArgumentException("Invalid check-in/check-out dates");
        }

        return booking.getAccommodation().getDailyRate()
                .multiply(BigDecimal.valueOf(numberOfDays));
    }
}
