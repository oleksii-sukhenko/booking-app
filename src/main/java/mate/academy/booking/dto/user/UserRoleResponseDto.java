package mate.academy.booking.dto.user;

import java.util.List;

public record UserRoleResponseDto(
        Long userId,
        String email,
        List<Long> roleIds
) {
}
