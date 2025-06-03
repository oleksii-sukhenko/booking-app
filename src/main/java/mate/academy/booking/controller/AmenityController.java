package mate.academy.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.accommodation.AmenityResponseDto;
import mate.academy.booking.dto.accommodation.CreateAmenityRequestDto;
import mate.academy.booking.service.accommodation.AmenityService;
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

@Tag(name = "Amenity management", description = "Endpoints for managing amenities")
@RestController
@RequiredArgsConstructor
@RequestMapping("/amenity")
public class AmenityController {
    private final AmenityService amenityService;

    @PostMapping
    @Operation(summary = "Create amenity", description = "Create amenity")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public AmenityResponseDto createAmenity(
            @RequestBody @Valid CreateAmenityRequestDto requestDto
    ) {
        return amenityService.save(requestDto);
    }

    @GetMapping
    @Operation(summary = "Find all amenities", description = "Find all available amenities")
    public Page<AmenityResponseDto> findAll(Pageable pageable) {
        return amenityService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get amenity", description = "Get amenity by its id")
    public AmenityResponseDto findById(@PathVariable Long id) {
        return amenityService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update amenity", description = "Update amenity by its id")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public void updateAmenityById(
            @PathVariable Long id,
            @RequestBody @Valid CreateAmenityRequestDto requestDto
    ) {
        amenityService.updateAmenityById(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete amenity", description = "Set amenity as deleted by its id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public void deleteAmenityById(@PathVariable Long id) {
        amenityService.deleteAmenityById(id);
    }
}
