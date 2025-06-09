package mate.academy.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.booking.BookingRequestDto;
import mate.academy.booking.dto.booking.BookingResponseDto;
import mate.academy.booking.dto.booking.BookingSearchParameters;
import mate.academy.booking.dto.booking.UpdateBookingRequestDto;
import mate.academy.booking.model.User;
import mate.academy.booking.service.booking.BookingService;
import mate.academy.booking.service.user.UserService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking management", description = "Endpoints for booking managing")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create booking", description = "Create booking")
    public BookingResponseDto create(@RequestBody @Valid BookingRequestDto requestDto) {
        User user = userService.getCurrentUser();
        return bookingService.create(requestDto, user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(
            summary = "Find booking by user id and status",
            description = "Find booking by user id and status"
    )
    public Page<BookingResponseDto> findByUserIdAndStatus(
            @ParameterObject Pageable pageable,
            @ParameterObject BookingSearchParameters searchParameters
    ) {
        return bookingService.findByUserIdAndStatus(pageable, searchParameters);
    }

    @GetMapping("/my")
    @Operation(summary = "Find my bookings", description = "Find bookings for authenticated user")
    public Page<BookingResponseDto> findForAuthenticatedUser(@ParameterObject Pageable pageable) {
        return bookingService.findForAuthenticatedUser(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Find booking by id", description = "Find booking by its id")
    public BookingResponseDto findById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update booking details",
            description = "Update booking details for authenticated user"
    )
    public BookingResponseDto updateBookingDetails(
            @PathVariable Long id,
            @RequestBody @Valid UpdateBookingRequestDto requestDto
    ) {
        return bookingService.updateBookingDetails(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Cancel booking",
            description = "Authenticated user can cancel his booking"
    )
    public void cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
    }
}
