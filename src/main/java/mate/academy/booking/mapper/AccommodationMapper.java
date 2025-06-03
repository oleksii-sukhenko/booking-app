package mate.academy.booking.mapper;

import mate.academy.booking.config.MapperConfig;
import mate.academy.booking.dto.accommodation.AccommodationResponseDto;
import mate.academy.booking.dto.accommodation.AccommodationResponseDtoWithoutAddressDto;
import mate.academy.booking.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.booking.model.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {
    AccommodationResponseDto toDto(Accommodation accommodation);

    AccommodationResponseDtoWithoutAddressDto toWithoutAddressDto(Accommodation accommodation);

    Accommodation toModel(CreateAccommodationRequestDto createAccommodationRequestDto);

    void updateAccommodationFromDto(
            CreateAccommodationRequestDto createAccommodationRequestDto,
            @MappingTarget Accommodation accommodation
    );
}
