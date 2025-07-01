package mate.academy.booking.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CreateAmenityRequestDto {
    @NotBlank
    private String name;
}
