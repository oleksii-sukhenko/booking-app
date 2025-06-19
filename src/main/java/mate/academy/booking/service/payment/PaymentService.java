package mate.academy.booking.service.payment;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.booking.dto.payment.PaymentResponseDto;
import mate.academy.booking.model.Booking;
import mate.academy.booking.model.Payment;
import mate.academy.booking.model.User;

public interface PaymentService {
    List<PaymentResponseDto> getPaymentSession(Long userId);

    PaymentResponseDto createPaymentSession(Long bookingId, User user);

    Payment createPaymentWithSession(Booking booking, BigDecimal amountToPay);

    String handleSuccess(String sessionId);

    String handleCancel(String sessionId);
}
