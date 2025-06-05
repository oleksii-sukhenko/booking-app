package mate.academy.booking.service.user;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.user.UpdatePasswordRequestDto;
import mate.academy.booking.dto.user.UpdatePasswordResponseDto;
import mate.academy.booking.dto.user.UpdateProfileRequestDto;
import mate.academy.booking.dto.user.UpdateUserRoleRequestDto;
import mate.academy.booking.dto.user.UserDataResponseDto;
import mate.academy.booking.dto.user.UserRegisterRequestDto;
import mate.academy.booking.dto.user.UserRoleResponseDto;
import mate.academy.booking.exception.RegisterException;
import mate.academy.booking.mapper.UserMapper;
import mate.academy.booking.mapper.UserRoleMapper;
import mate.academy.booking.model.Role;
import mate.academy.booking.model.User;
import mate.academy.booking.repository.user.UserRepository;
import mate.academy.booking.repository.user.role.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String PASSWORD_CHANGED = "Password successfully changed";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserDataResponseDto register(UserRegisterRequestDto requestDto)
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

    @Override
    public UserDataResponseDto profileInfo() {
        return userMapper.toDto(getCurrentUser());
    }

    @Override
    public UserDataResponseDto updateProfile(UpdateProfileRequestDto requestDto) {
        User user = getCurrentUser();

        if (hasText(requestDto.getEmail())) {
            user.setEmail(requestDto.getEmail());
        }

        if (hasText(requestDto.getFirstName())) {
            user.setFirstName(requestDto.getFirstName());
        }

        if (hasText(requestDto.getLastName())) {
            user.setLastName(requestDto.getLastName());
        }

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UpdatePasswordResponseDto changePassword(UpdatePasswordRequestDto requestDto) {
        User user = getCurrentUser();

        if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (!requestDto.getNewPassword().equals(requestDto.getRepeatNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be differ from old one");
        }

        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(user);
        return new UpdatePasswordResponseDto(PASSWORD_CHANGED);
    }

    @Override
    public UserRoleResponseDto updateRole(Long userId, UpdateUserRoleRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Role newRole = roleRepository.findById(requestDto.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        List<Long> roleIds = user.getRoles().stream()
                .map(Role::getId)
                .toList();

        user.setRoles(new HashSet<>(List.of(newRole)));
        userRepository.save(user);
        return new UserRoleResponseDto(user.getId(), user.getEmail(), roleIds);
    }

    @Override
    public Page<UserRoleResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userRoleMapper::toDto);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
