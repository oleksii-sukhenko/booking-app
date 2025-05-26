package mate.academy.booking.mapper;

import mate.academy.booking.config.MapperConfig;
import mate.academy.booking.dto.user.UserRegisterRequestDto;
import mate.academy.booking.dto.user.UserRegisterResponseDto;
import mate.academy.booking.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserRegisterResponseDto toDto(User user);

    User toModel(UserRegisterRequestDto userRegisterRequestDto);
}
