package mate.academy.booking.dto.accommodation;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.booking.model.Accommodation;

public record AccommodationResponseDtoWithoutAddressDto(
        Long id,
        Accommodation.Type type,
        String size,
        List<AmenityResponseDto> amenities,
        BigDecimal dailyRate,
        int availability
) {
}
