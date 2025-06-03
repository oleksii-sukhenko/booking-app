package mate.academy.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.accommodation.AddressResponseDto;
import mate.academy.booking.dto.accommodation.CreateAddressRequestDto;
import mate.academy.booking.service.accommodation.AddressService;
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

@Tag(name = "Location management", description = "Endpoints for managing locations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/location")
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    @Operation(summary = "Create address", description = "Create address")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public AddressResponseDto createAddress(
            @RequestBody @Valid CreateAddressRequestDto requestDto
    ) {
        return addressService.save(requestDto);
    }

    @GetMapping
    @Operation(summary = "Find all Locations", description = "Find all available locations")
    public Page<AddressResponseDto> findAll(Pageable pageable) {
        return addressService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get location", description = "Get location by its id")
    public AddressResponseDto findById(@PathVariable Long id) {
        return addressService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update address", description = "Update address by its id")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public void updateAddressById(
            @PathVariable Long id,
            @RequestBody @Valid CreateAddressRequestDto requestDto
    ) {
        addressService.updateAddressById(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete location", description = "Set address as deleted by its id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public void deleteAddressById(@PathVariable Long id) {
        addressService.deleteAddressById(id);
    }
}
