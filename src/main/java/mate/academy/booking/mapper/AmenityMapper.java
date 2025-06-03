package mate.academy.booking.mapper;

import mate.academy.booking.config.MapperConfig;
import mate.academy.booking.dto.accommodation.AmenityResponseDto;
import mate.academy.booking.dto.accommodation.CreateAmenityRequestDto;
import mate.academy.booking.model.Amenity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface AmenityMapper {
    AmenityResponseDto toDto(Amenity amenity);

    Amenity toModel(CreateAmenityRequestDto createAmenityRequestDto);

    void updateAmenityFromDto(
            CreateAmenityRequestDto requestDto,
            @MappingTarget Amenity amenity
    );
}
