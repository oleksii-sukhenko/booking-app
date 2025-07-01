package mate.academy.booking.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpdateBookingRequestDto {
    @FutureOrPresent
    private LocalDate checkInDate;
    @Future
    private LocalDate checkOutDate;
    @Positive
    private Long accommodationId;
}
