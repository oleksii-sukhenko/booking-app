package mate.academy.booking.service.accommodation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.accommodation.AddressResponseDto;
import mate.academy.booking.dto.accommodation.CreateAddressRequestDto;
import mate.academy.booking.mapper.AddressMapper;
import mate.academy.booking.model.Address;
import mate.academy.booking.repository.accommodation.AddressRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public AddressResponseDto save(CreateAddressRequestDto requestDto) {
        Address address = addressMapper.toModel(requestDto);
        return addressMapper.toDto(addressRepository.save(address));
    }

    @Override
    public Page<AddressResponseDto> findAll(Pageable pageable) {
        return addressRepository.findAll(pageable)
                .map(addressMapper::toDto);
    }

    @Override
    public AddressResponseDto findById(Long id) {
        Address address = addressRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find address with ID: " + id)
        );
        return addressMapper.toDto(address);
    }

    @Override
    public void updateAddressById(Long id, CreateAddressRequestDto requestDto) {
        Address address = addressRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find address with ID: " + id)
        );
        addressMapper.updateAddressFromDto(requestDto, address);
        addressRepository.save(address);
    }

    @Override
    public void deleteAddressById(Long id) {
        addressRepository.deleteById(id);
    }
}
