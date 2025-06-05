package mate.academy.booking.dto.user;

public record UserDataResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName
) {
}
