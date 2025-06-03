package mate.academy.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.user.UserLoginRequestDto;
import mate.academy.booking.dto.user.UserLoginResponseDto;
import mate.academy.booking.dto.user.UserRegisterRequestDto;
import mate.academy.booking.dto.user.UserRegisterResponseDto;
import mate.academy.booking.exception.RegisterException;
import mate.academy.booking.security.AuthenticationService;
import mate.academy.booking.service.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication management", description = "Endpoints for authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Register user", description = "Register a new user")
    public UserRegisterResponseDto register(
            @RequestBody @Valid UserRegisterRequestDto requestDto
    )
            throws RegisterException {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Login an existing user")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
