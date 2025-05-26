package mate.academy.booking.dto.user;

public record UserRegisterResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName
) {
}
