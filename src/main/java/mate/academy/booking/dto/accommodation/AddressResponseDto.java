package mate.academy.booking.dto.accommodation;

public record AddressResponseDto(
        Long id,
        String country,
        String city,
        String street,
        String number,
        String postcode
) {
}
