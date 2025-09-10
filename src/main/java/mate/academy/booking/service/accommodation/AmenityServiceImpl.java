package mate.academy.booking.service.accommodation;

import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.accommodation.AmenityResponseDto;
import mate.academy.booking.dto.accommodation.CreateAmenityRequestDto;
import mate.academy.booking.exception.EntityNotFoundException;
import mate.academy.booking.mapper.AmenityMapper;
import mate.academy.booking.model.Amenity;
import mate.academy.booking.repository.accommodation.AmenityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {
    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    @Override
    public AmenityResponseDto save(CreateAmenityRequestDto requestDto) {
        Amenity amenity = amenityMapper.toModel(requestDto);
        return amenityMapper.toDto(amenityRepository.save(amenity));
    }

    @Override
    public Page<AmenityResponseDto> findAll(Pageable pageable) {
        return amenityRepository.findAll(pageable)
                .map(amenityMapper::toDto);
    }

    @Override
    public AmenityResponseDto findById(Long id) {
        Amenity amenity = amenityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find amenity with ID: " + id)
        );
        return amenityMapper.toDto(amenity);
    }

    @Override
    public void updateAmenityById(Long id, CreateAmenityRequestDto requestDto) {
        Amenity amenity = amenityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find amenity with ID: " + id)
        );
        amenityMapper.updateAmenityFromDto(requestDto, amenity);
        amenityRepository.save(amenity);
    }

    @Override
    public void deleteAmenityById(Long id) {
        amenityRepository.deleteById(id);
    }
}
