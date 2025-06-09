package mate.academy.booking.dto.booking;

public record BookingSearchParameters(
        Long[] userId,
        String[] status
) {
}
