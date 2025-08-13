package mate.academy.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.user.UpdatePasswordRequestDto;
import mate.academy.booking.dto.user.UpdatePasswordResponseDto;
import mate.academy.booking.dto.user.UpdateProfileRequestDto;
import mate.academy.booking.dto.user.UpdateUserRoleRequestDto;
import mate.academy.booking.dto.user.UserDataResponseDto;
import mate.academy.booking.dto.user.UserRoleResponseDto;
import mate.academy.booking.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for user managing")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get all users", description = "Get list of all users")
    Page<UserRoleResponseDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Update user role", description = "Update user role by user id")
    public UserRoleResponseDto updateRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRoleRequestDto requestDto
    ) {
        return userService.updateRole(id, requestDto);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get profile information",
            description = "Get profile information about authenticated user"
    )
    public UserDataResponseDto profileInfo() {
        return userService.profileInfo();
    }

    @PatchMapping("/me/info")
    @Operation(
            summary = "Update user info",
            description = "Update info about authenticated user"
    )
    public UserDataResponseDto updateProfile(UpdateProfileRequestDto requestDto) {
        return userService.updateProfile(requestDto);
    }

    @PatchMapping("/me/password")
    @Operation(
            summary = "Update user password",
            description = "Update password of authenticated user"
    )
    public UpdatePasswordResponseDto changePassword(UpdatePasswordRequestDto requestDto) {
        return userService.changePassword(requestDto);
    }
}
