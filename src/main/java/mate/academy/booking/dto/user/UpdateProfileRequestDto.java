package mate.academy.booking.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequestDto {
    @Email
    private String email;
    @Size(max = 32)
    private String firstName;
    @Size(max = 32)
    private String lastName;
}
