package mate.academy.booking.service.user;

import mate.academy.booking.dto.user.UserRegisterRequestDto;
import mate.academy.booking.dto.user.UserRegisterResponseDto;
import mate.academy.booking.exception.RegisterException;

public interface UserService {
    UserRegisterResponseDto register(UserRegisterRequestDto requestDto)
            throws RegisterException;
}
