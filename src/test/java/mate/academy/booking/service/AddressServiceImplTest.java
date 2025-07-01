package mate.academy.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import mate.academy.booking.dto.accommodation.AddressResponseDto;
import mate.academy.booking.dto.accommodation.CreateAddressRequestDto;
import mate.academy.booking.mapper.AddressMapper;
import mate.academy.booking.model.Address;
import mate.academy.booking.repository.accommodation.AddressRepository;
import mate.academy.booking.service.accommodation.AddressServiceImpl;
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
public class AddressServiceImplTest {
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AddressMapper addressMapper;
    @InjectMocks
    private AddressServiceImpl addressService;

    private CreateAddressRequestDto requestDto;
    private Address address;
    private AddressResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new CreateAddressRequestDto()
                .setCountry("Ukraine")
                .setCity("Kyiv")
                .setStreet("Khreshcatyk")
                .setNumber("1")
                .setPostCode("02000");

        address = new Address()
                .setId(1L)
                .setCountry("Ukraine")
                .setCity("Kyiv")
                .setStreet("Khreshcatyk")
                .setNumber("1")
                .setPostcode("02000");

        responseDto = new AddressResponseDto(
                address.getId(),
                address.getCountry(),
                address.getCity(),
                address.getStreet(),
                address.getNumber(),
                address.getPostcode()
                );
    }

    @Test
    void save_ValidDto_ShouldReturnResponseDto() {
        when(addressMapper.toModel(requestDto)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(responseDto);

        AddressResponseDto actual = addressService.save(requestDto);

        assertEquals(responseDto, actual);
    }

    @Test
    void findAll_Valid_ShouldReturnPagedDtos() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Address> page = new PageImpl<>(List.of(address));
        when(addressRepository.findAll(pageable)).thenReturn(page);
        when(addressMapper.toDto(address)).thenReturn(responseDto);

        Page<AddressResponseDto> result = addressService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(responseDto, result.getContent().getFirst());
    }

    @Test
    void findById_ValidId_ShouldReturnDto() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressMapper.toDto(address)).thenReturn(responseDto);

        AddressResponseDto actual = addressService.findById(1L);

        assertEquals(responseDto, actual);
    }

    @Test
    void findById_InvalidId_ShouldThrowException() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> addressService.findById(1L)
                );
        assertEquals("Can't find address with ID: 1", exception.getMessage());
    }

    @Test
    void updateAddressById_ValidId_ShouldUpdateAndSave() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        addressService.updateAddressById(1L, requestDto);

        verify(addressMapper).updateAddressFromDto(requestDto, address);
        verify(addressRepository).save(address);
    }

    @Test
    void updateAddressById_InvalidId_ShouldThrowException() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> addressService.updateAddressById(1L, requestDto)
        );

        assertEquals("Can't find address with ID: 1", exception.getMessage());
        verify(addressRepository, never()).save(any());
    }

    @Test
    void deleteAddressById_ValidId_ShouldDeleteAddress() {
        addressService.deleteAddressById(1L);
        verify(addressRepository).deleteById(1L);
    }
}
