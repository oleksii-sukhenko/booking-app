package mate.academy.booking.mapper;

import mate.academy.booking.config.MapperConfig;
import mate.academy.booking.dto.accommodation.AddressResponseDto;
import mate.academy.booking.dto.accommodation.CreateAddressRequestDto;
import mate.academy.booking.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface AddressMapper {
    AddressResponseDto toDto(Address address);

    @Mapping(source = "postCode", target = "postcode")
    Address toModel(CreateAddressRequestDto createAddressRequestDto);

    void updateAddressFromDto(
            CreateAddressRequestDto createAddressRequestDto,
            @MappingTarget Address address
    );
}
