package mate.academy.booking.dto.payment;

import java.math.BigDecimal;
import mate.academy.booking.model.Payment.Status;

public record PaymentResponseDto(
        Long id,
        Long bookingId,
        BigDecimal amountToPay,
        String sessionUrl,
        String sessionId,
        Status status
) {
}
