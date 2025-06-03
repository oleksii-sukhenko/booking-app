package mate.academy.booking.service.accommodation;

import mate.academy.booking.dto.accommodation.AmenityResponseDto;
import mate.academy.booking.dto.accommodation.CreateAmenityRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AmenityService {
    AmenityResponseDto save(CreateAmenityRequestDto requestDto);

    Page<AmenityResponseDto> findAll(Pageable pageable);

    AmenityResponseDto findById(Long id);

    void updateAmenityById(Long id, CreateAmenityRequestDto requestDto);

    void deleteAmenityById(Long id);
}
