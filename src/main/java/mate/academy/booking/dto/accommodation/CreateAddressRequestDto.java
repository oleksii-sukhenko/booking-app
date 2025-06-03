package mate.academy.booking.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAddressRequestDto {
    @NotBlank
    private String country;
    @NotBlank
    private String city;
    @NotBlank
    private String street;
    @NotBlank
    private String number;
    @NotBlank
    private String postCode;
    @NotNull
    @Size(min = 1)
    private List<@NotNull @Positive Long> accommodationIds;
}
