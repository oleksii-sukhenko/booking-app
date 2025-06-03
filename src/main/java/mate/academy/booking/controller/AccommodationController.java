package mate.academy.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.accommodation.AccommodationResponseDto;
import mate.academy.booking.dto.accommodation.AccommodationResponseDtoWithoutAddressDto;
import mate.academy.booking.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.booking.service.accommodation.AccommodationService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodation management", description = "Endpoints for managing accommodations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/accommodation")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @PostMapping
    @Operation(summary = "Create a new accommodation", description = "Create a new accommodation")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public AccommodationResponseDto createAccommodation(
            @RequestBody @Valid CreateAccommodationRequestDto requestDto
    ) {
        return accommodationService.save(requestDto);
    }

    @GetMapping
    @Operation(
            summary = "Find all accommodations",
            description = "Get list of all available accommodations"
    )
    public Page<AccommodationResponseDto> findAll(Pageable pageable) {
        return accommodationService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get accommodation by id",
            description = "Get accommodation by id"
    )
    public AccommodationResponseDto findById(@PathVariable Long id) {
        return accommodationService.findById(id);
    }

    @GetMapping("/by-address/{addressId}")
    @Operation(
            summary = "Get accommodations by address",
            description = "Get list of all available accommodations by address ID"
    )
    public Page<AccommodationResponseDtoWithoutAddressDto> findAvailableByAddressId(
            @PathVariable Long addressId, Pageable pageable
    ) {
        return accommodationService.findAvailableByAddressId(addressId, pageable);
    }

    @GetMapping("/by-amenities")
    @Operation(

            summary = "Get accommodations by amenity IDs",
            description = "Get list of all available accommodations which include asked amenity IDs"
    )
    public Page<AccommodationResponseDto> findAvailableByAmenityIds(
            @RequestParam List<Long> amenityIds,
            Pageable pageable
    ) {
        return accommodationService.findAllByAmenityIds(amenityIds, pageable);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update accommodation",
            description = "Update accommodation by its ID"
    )
    public void updateAccommodationById(
            @PathVariable Long id,
            @RequestBody @Valid CreateAccommodationRequestDto requestDto
    ) {
        accommodationService.updateAccommodationById(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete accommodation",
            description = "Set accommodation as deleted by its id"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccommodationById(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }
}
