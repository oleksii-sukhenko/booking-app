package mate.academy.booking.service.user;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.user.UserRegisterRequestDto;
import mate.academy.booking.dto.user.UserRegisterResponseDto;
import mate.academy.booking.exception.RegisterException;
import mate.academy.booking.mapper.UserMapper;
import mate.academy.booking.model.Role;
import mate.academy.booking.model.User;
import mate.academy.booking.repository.user.UserRepository;
import mate.academy.booking.repository.user.role.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserRegisterResponseDto register(UserRegisterRequestDto requestDto)
            throws RegisterException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegisterException("This email is already taken");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByRole(Role.RoleName.ROLE_CUSTOMER);
        user.setRoles(Set.of(role));
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
