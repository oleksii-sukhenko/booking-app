package mate.academy.booking.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import mate.academy.booking.model.Accommodation;

@Getter
@Setter
public class CreateAccommodationRequestDto {
    @NotNull
    private Accommodation.Type type;
    @NotNull
    @Positive
    private Long addressID;
    @NotBlank
    private String size;
    @NotNull
    @Size(min = 1)
    private List<@NotNull @Positive Long> amenityIds;
    @NotNull
    @Positive
    private BigDecimal dailyRate;
    @PositiveOrZero
    private int availability;
}
