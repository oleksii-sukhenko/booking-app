package mate.academy.booking.service.user;

import mate.academy.booking.dto.user.UpdatePasswordRequestDto;
import mate.academy.booking.dto.user.UpdatePasswordResponseDto;
import mate.academy.booking.dto.user.UpdateProfileRequestDto;
import mate.academy.booking.dto.user.UpdateUserRoleRequestDto;
import mate.academy.booking.dto.user.UserDataResponseDto;
import mate.academy.booking.dto.user.UserRegisterRequestDto;
import mate.academy.booking.dto.user.UserRoleResponseDto;
import mate.academy.booking.exception.RegisterException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDataResponseDto register(UserRegisterRequestDto requestDto)
            throws RegisterException;

    UserDataResponseDto profileInfo();

    UserDataResponseDto updateProfile(UpdateProfileRequestDto requestDto);

    UpdatePasswordResponseDto changePassword(UpdatePasswordRequestDto requestDto);

    UserRoleResponseDto updateRole(Long userId, UpdateUserRoleRequestDto requestDto);

    Page<UserRoleResponseDto> getAllUsers(Pageable pageable);
}
