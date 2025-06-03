package mate.academy.booking.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAmenityRequestDto {
    @NotBlank
    private String amenityName;
}
