package mate.academy.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.booking.dto.accommodation.AccommodationResponseDto;
import mate.academy.booking.dto.accommodation.AddressResponseDto;
import mate.academy.booking.dto.accommodation.AmenityResponseDto;
import mate.academy.booking.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.booking.exception.EntityNotFoundException;
import mate.academy.booking.mapper.AccommodationMapper;
import mate.academy.booking.model.Accommodation;
import mate.academy.booking.model.Address;
import mate.academy.booking.model.Amenity;
import mate.academy.booking.repository.accommodation.AccommodationRepository;
import mate.academy.booking.repository.accommodation.AddressRepository;
import mate.academy.booking.repository.accommodation.AmenityRepository;
import mate.academy.booking.service.accommodation.AccommodationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceImplTest {
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AccommodationMapper accommodationMapper;
    @Mock
    private AmenityRepository amenityRepository;
    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    private Address address;
    private Accommodation accommodation;
    private AccommodationResponseDto accommodationResponseDto;
    private CreateAccommodationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = new CreateAccommodationRequestDto()
                .setType(Accommodation.Type.CONDO)
                .setAddressID(1L)
                .setSize("1BR")
                .setAmenityIds(List.of(1L, 2L))
                .setDailyRate(BigDecimal.valueOf(10))
                .setAvailability(2);

        address = new Address()
                .setId(1L)
                .setCountry("Ukraine")
                .setCity("Kyiv")
                .setStreet("Vasylenka")
                .setNumber("1a")
                .setPostcode("02000");

        AddressResponseDto addressResponseDto = new AddressResponseDto(
                address.getId(),
                address.getCountry(),
                address.getCity(),
                address.getStreet(),
                address.getNumber(),
                address.getPostcode()
        );

        Amenity wifi = new Amenity().setId(1L).setName("WiFi");
        Amenity tv = new Amenity().setId(2L).setName("TV");

        Set<Amenity> amenities = Set.of(wifi, tv);
        List<AmenityResponseDto> amenityResponseDtos = List.of(
                new AmenityResponseDto(1L, "WiFi"),
                new AmenityResponseDto(2L, "TV")
        );

        accommodation = new Accommodation()
                .setId(1L)
                .setType(Accommodation.Type.APARTMENT)
                .setSize("Large")
                .setDailyRate(BigDecimal.TEN)
                .setAvailability(2)
                .setLocation(address)
                .setAmenities(amenities);

        accommodationResponseDto = new AccommodationResponseDto(
                accommodation.getId(),
                accommodation.getType(),
                addressResponseDto,
                accommodation.getSize(),
                amenityResponseDtos,
                accommodation.getDailyRate(),
                accommodation.getAvailability()
        );
    }

    @Test
    void findById_Exists_ShouldReturnDto() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toDto(accommodation)).thenReturn(accommodationResponseDto);
        assertEquals(accommodationResponseDto, accommodationService.findById(1L));
    }

    @Test
    void findById_NotFound_ShouldThrowException() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.findById(1L));
        assertEquals("Can't find accommodation with ID: 1", exception.getMessage());
    }

    @Test
    void save_ValidDto_ShouldSaveAndReturnDto() {
        Accommodation savedAccommodation = new Accommodation().setId(2L);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(amenityRepository.findById(1L)).thenReturn(Optional.of(
                new Amenity().setId(1L).setName("WiFi")
        ));
        when(amenityRepository.findById(2L)).thenReturn(Optional.of(
                new Amenity().setId(2L).setName("TV")
        ));
        when(accommodationMapper.toModel(requestDto)).thenReturn(savedAccommodation);
        when(accommodationRepository.save(any(Accommodation.class)))
                .thenReturn(savedAccommodation);
        when(accommodationMapper.toDto(any(Accommodation.class)))
                .thenReturn(accommodationResponseDto);

        AccommodationResponseDto actual = accommodationService.save(requestDto);

        assertEquals(accommodationResponseDto, actual);
        verify(accommodationRepository).save(savedAccommodation);
    }

    @Test
    void save_NoAddress_ShouldThrowException() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> accommodationService.save(requestDto)
        );

        assertEquals("Can't find address with ID: 1", exception.getMessage());
        verify(accommodationRepository, never()).save(any());
    }

    @Test
    void updateAccommodationById_WrongId_ShouldThrowException() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> accommodationService.updateAccommodationById(1L, requestDto)
        );

        assertEquals("Can't find accommodation with ID: 1", exception.getMessage());
        verify(accommodationRepository, never()).save(any());
    }

    @Test
    void deleteById_ValidId_ShouldSoftDelete() {
        accommodation.setDeleted(false);
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));

        accommodationService.deleteById(1L);

        assertTrue(accommodation.isDeleted());
        verify(accommodationRepository).save(accommodation);
    }

    @Test
    void deleteById_NotFound_ShouldThrowException() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> accommodationService.deleteById(1L)
        );

        assertEquals("Accommodation not found", exception.getMessage());
        verify(accommodationRepository, never()).save(any());
    }
}
