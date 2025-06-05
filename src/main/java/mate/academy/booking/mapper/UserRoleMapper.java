package mate.academy.booking.mapper;

import java.util.List;
import java.util.Set;
import mate.academy.booking.config.MapperConfig;
import mate.academy.booking.dto.user.UpdateUserRoleRequestDto;
import mate.academy.booking.dto.user.UserRoleResponseDto;
import mate.academy.booking.model.Role;
import mate.academy.booking.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserRoleMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "roleIds", expression = "java(mapRoleIds(user.getRoles()))")
    UserRoleResponseDto toDto(User user);

    User toModel(UpdateUserRoleRequestDto requestDto);

    default List<Long> mapRoleIds(Set<Role> roles) {
        return roles.stream()
                .map(Role::getId)
                .toList();
    }
}
