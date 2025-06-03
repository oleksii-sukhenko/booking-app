package mate.academy.booking.service.accommodation;

import mate.academy.booking.dto.accommodation.AddressResponseDto;
import mate.academy.booking.dto.accommodation.CreateAddressRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AddressService {
    AddressResponseDto save(CreateAddressRequestDto requestDto);

    Page<AddressResponseDto> findAll(Pageable pageable);

    AddressResponseDto findById(Long id);

    void updateAddressById(Long id, CreateAddressRequestDto requestDto);

    void deleteAddressById(Long id);
}
