package mate.academy.booking.service.accommodation;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.accommodation.AccommodationResponseDto;
import mate.academy.booking.dto.accommodation.AccommodationResponseDtoWithoutAddressDto;
import mate.academy.booking.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.booking.mapper.AccommodationMapper;
import mate.academy.booking.model.Accommodation;
import mate.academy.booking.model.Address;
import mate.academy.booking.model.Amenity;
import mate.academy.booking.repository.accommodation.AccommodationRepository;
import mate.academy.booking.repository.accommodation.AddressRepository;
import mate.academy.booking.repository.accommodation.AmenityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AddressRepository addressRepository;
    private final AmenityRepository amenityRepository;
    private final AccommodationMapper accommodationMapper;

    @Override
    public AccommodationResponseDto save(CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModel(requestDto);
        setAddressAndAmenities(accommodation, requestDto);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public Page<AccommodationResponseDto> findAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable)
                .map(accommodationMapper::toDto);
    }

    @Override
    public AccommodationResponseDto findById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find accommodation with ID: " + id)
        );
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public Page<AccommodationResponseDtoWithoutAddressDto> findAvailableByAddressId(
            Long addressId,
            Pageable pageable
    ) {
        return accommodationRepository
                .findByLocationIdAndAvailabilityGreaterThan(addressId, 0, pageable)
                .map(accommodationMapper::toWithoutAddressDto);
    }

    @Override
    public Page<AccommodationResponseDto> findAllByAmenityIds(
            List<Long> amenityIds, Pageable pageable
    ) {
        if (amenityIds == null || amenityIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Accommodation> accommodations = accommodationRepository.findByAmenities(
                amenityIds, amenityIds.size(), pageable
        );
        return accommodations.map(accommodationMapper::toDto);
    }

    @Override
    public void updateAccommodationById(Long id, CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find accommodation with ID: " + id)
        );

        accommodationMapper.updateAccommodationFromDto(requestDto, accommodation);
        setAddressAndAmenities(accommodation, requestDto);
        accommodationRepository.save(accommodation);
    }

    @Override
    public void deleteById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accommodation not found"));
        accommodation.setDeleted(true);
        accommodationRepository.save(accommodation);
    }

    private void setAddressAndAmenities(
            Accommodation accommodation,
            CreateAccommodationRequestDto requestDto
    ) {
        Address address = addressRepository.findById(requestDto.getAddressID())
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't find address with ID: " + requestDto.getAddressID()
                        )
                );
        accommodation.setLocation(address);

        Set<Amenity> amenities = requestDto.getAmenityIds().stream()
                .map(id -> amenityRepository.findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException(
                                        "Can't find amenity with ID: " + id)
                        )
                )
                .collect(Collectors.toSet());
        accommodation.setAmenities(amenities);
    }
}
