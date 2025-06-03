package mate.academy.booking.service.accommodation;

import java.util.List;
import mate.academy.booking.dto.accommodation.AccommodationResponseDto;
import mate.academy.booking.dto.accommodation.AccommodationResponseDtoWithoutAddressDto;
import mate.academy.booking.dto.accommodation.CreateAccommodationRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    AccommodationResponseDto save(CreateAccommodationRequestDto requestDto);

    Page<AccommodationResponseDto> findAll(Pageable pageable);

    AccommodationResponseDto findById(Long id);

    Page<AccommodationResponseDtoWithoutAddressDto> findAvailableByAddressId(
            Long addressId, Pageable pageable
    );

    Page<AccommodationResponseDto> findAllByAmenityIds(
            List<Long> amenityIds,
            Pageable pageable
    );

    void updateAccommodationById(Long id, CreateAccommodationRequestDto requestDto);

    void deleteById(Long id);
}
