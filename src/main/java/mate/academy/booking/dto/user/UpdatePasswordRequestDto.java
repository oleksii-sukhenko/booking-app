package mate.academy.booking.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpdatePasswordRequestDto {
    @Size(max = 16)
    private String oldPassword;
    @Size(max = 16)
    private String newPassword;
    @Size(max = 16)
    private String repeatNewPassword;
}
