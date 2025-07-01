package mate.academy.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityNotFoundException;
import mate.academy.booking.dto.accommodation.AmenityResponseDto;
import mate.academy.booking.dto.accommodation.CreateAmenityRequestDto;
import mate.academy.booking.mapper.AmenityMapper;
import mate.academy.booking.model.Amenity;
import mate.academy.booking.repository.accommodation.AmenityRepository;
import mate.academy.booking.service.accommodation.AmenityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class AmenityServiceImplTest {
    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private AmenityMapper amenityMapper;
    @InjectMocks
    private AmenityServiceImpl amenityService;

    private CreateAmenityRequestDto requestDto;
    private Amenity amenity;
    private AmenityResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new CreateAmenityRequestDto();
        requestDto.setName("WiFi");

        amenity = new Amenity()
                .setId(1L)
                .setName("WiFi");

        responseDto = new AmenityResponseDto(
                amenity.getId(),
                amenity.getName()
        );
    }

    @Test
    void save_ValidDto_ShouldReturnResponseDto() {
        when(amenityMapper.toModel(requestDto)).thenReturn(amenity);
        when(amenityRepository.save(amenity)).thenReturn(amenity);
        when(amenityMapper.toDto(amenity)).thenReturn(responseDto);

        AmenityResponseDto actual = amenityService.save(requestDto);

        assertEquals(responseDto, actual);
    }

    @Test
    void findAll_Valid_ShouldReturnPageDtos() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Amenity> page = new PageImpl<>(List.of(amenity));
        when(amenityRepository.findAll(pageable)).thenReturn(page);
        when(amenityMapper.toDto(amenity)).thenReturn(responseDto);

        Page<AmenityResponseDto> result = amenityService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(responseDto, result.getContent().getFirst());
    }

    @Test
    void findById_ValidId_ShouldReturnDto() {
        when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
        when(amenityMapper.toDto(amenity)).thenReturn(responseDto);

        AmenityResponseDto actual = amenityService.findById(1L);

        assertEquals(responseDto, actual);
    }

    @Test
    void findById_InvalidId_ShouldThrowException() {
        when(amenityRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> amenityService.findById(1L)
        );

        assertEquals("Can't find amenity with ID: 1", exception.getMessage());
    }

    @Test
    void updateAmenityById_ValidId_ShouldUpdateAndSave() {
        when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));

        amenityService.updateAmenityById(1L, requestDto);

        verify(amenityMapper).updateAmenityFromDto(requestDto, amenity);
        verify(amenityRepository).save(amenity);
    }

    @Test
    void updateAmenityById_InvalidId_ShouldThrowException() {
        when(amenityRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> amenityService.updateAmenityById(1L, requestDto)
        );

        assertEquals("Can't find amenity with ID: 1", exception.getMessage());
        verify(amenityRepository, never()).save(any());
    }

    @Test
    void deleteAmenityById_ValidId_ShouldDelete() {
        amenityService.deleteAmenityById(1L);
        verify(amenityRepository).deleteById(1L);
    }
}
