package mate.academy.booking.dto.booking;

import java.time.LocalDate;
import mate.academy.booking.model.Booking;

public record BookingResponseDto(
        Long id,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Long accommodationId,
        Long userId,
        Booking.Status status
) {
}
