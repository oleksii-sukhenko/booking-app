package mate.academy.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;
import mate.academy.booking.dto.user.UpdatePasswordRequestDto;
import mate.academy.booking.dto.user.UpdatePasswordResponseDto;
import mate.academy.booking.dto.user.UpdateProfileRequestDto;
import mate.academy.booking.dto.user.UpdateUserRoleRequestDto;
import mate.academy.booking.dto.user.UserDataResponseDto;
import mate.academy.booking.dto.user.UserRegisterRequestDto;
import mate.academy.booking.dto.user.UserRoleResponseDto;
import mate.academy.booking.exception.RegisterException;
import mate.academy.booking.mapper.UserMapper;
import mate.academy.booking.model.Role;
import mate.academy.booking.model.User;
import mate.academy.booking.repository.user.UserRepository;
import mate.academy.booking.repository.user.role.RoleRepository;
import mate.academy.booking.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    UserServiceImpl userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@mail.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        user = new User()
                .setId(1L)
                .setEmail("user@mail.com")
                .setFirstName("username")
                .setLastName("userLastname")
                .setPassword("encodedPassword")
                .setRoles(Set.of());

        role = new Role().setId(1L).setRole(Role.RoleName.ROLE_CUSTOMER);
    }

    @Test
    void register_ValidRequest_ShouldSaveAndReturnDto() throws RegisterException {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto()
                .setEmail("user@mail.com")
                .setPassword("password");
        UserDataResponseDto expectedDto = new UserDataResponseDto(
                user.getId(), user.getEmail(), user.getFirstName(), user.getLastName()
        );

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByRole(Role.RoleName.ROLE_CUSTOMER)).thenReturn(role);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDataResponseDto actual = userService.register(requestDto);

        assertEquals(expectedDto, actual);
        verify(userRepository).save(user);
    }

    @Test
    void register_EmailExists_ShouldThrowException() {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto()
                .setEmail("user@mail.com");

        when(userRepository.existsByEmail("user@mail.com")).thenReturn(true);

        assertThrows(RegisterException.class,
                () -> userService.register(requestDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_ValidRequest_ShouldUpdatePassword() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto()
                .setOldPassword("oldPassword")
                .setNewPassword("newPassword")
                .setRepeatNewPassword("newPassword");

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(
                "oldPassword", "encodedPassword"
        )).thenReturn(true);
        when(passwordEncoder.matches(
                "newPassword", "encodedPassword"
        )).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncoded");

        UpdatePasswordResponseDto result = userService.changePassword(requestDto);

        assertEquals("Password successfully changed", result.passwordChanged());
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_WrongOldPassword_ShouldThrowException() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto()
                .setOldPassword("wrongPassword")
                .setNewPassword("newPassword")
                .setRepeatNewPassword("newPassword");

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(
                "wrongPassword", "encodedPassword"
        )).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword(requestDto));
    }

    @Test
    void updateProfile_ValidRequest_ShouldUpdateFields() {
        UpdateProfileRequestDto requestDto = new UpdateProfileRequestDto()
                .setEmail("new@mail.com")
                .setFirstName("newName")
                .setLastName("newLastname");
        UserDataResponseDto dto = new UserDataResponseDto(
                user.getId(), user.getEmail(), user.getFirstName(), user.getLastName()
        );

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDataResponseDto result = userService.updateProfile(requestDto);

        assertEquals(dto, result);
        verify(userRepository).save(user);
    }

    @Test
    void updateRole_ValidRequest_ShouldUpdateRole() {
        UpdateUserRoleRequestDto requestDto = new UpdateUserRoleRequestDto();
        requestDto.setRoleId(2L);

        Role newRole = new Role().setId(2L).setRole(Role.RoleName.ROLE_MANAGER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(newRole));

        UserRoleResponseDto dto = userService.updateRole(1L, requestDto);

        assertEquals(user.getId(), dto.userId());
        assertEquals(user.getEmail(), dto.email());
    }

    @Test
    void updateRole_UserNotFound_ShouldThrowException() {
        UpdateUserRoleRequestDto requestDto = new UpdateUserRoleRequestDto();
        requestDto.setRoleId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.updateRole(1L, requestDto));
    }
}
