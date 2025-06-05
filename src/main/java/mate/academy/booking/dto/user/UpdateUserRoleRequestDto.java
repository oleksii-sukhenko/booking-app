package mate.academy.booking.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRoleRequestDto {
    @NotNull
    @Positive
    @Max(2)
    private Long roleId;
}
