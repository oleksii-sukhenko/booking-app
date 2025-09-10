package mate.academy.booking.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import mate.academy.booking.dto.accommodation.AccommodationResponseDto;
import mate.academy.booking.dto.accommodation.AccommodationResponseDtoWithoutAddressDto;
import mate.academy.booking.dto.accommodation.AddressResponseDto;
import mate.academy.booking.dto.accommodation.AmenityResponseDto;
import mate.academy.booking.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.booking.dto.accommodation.CreateAddressRequestDto;
import mate.academy.booking.dto.accommodation.CreateAmenityRequestDto;
import mate.academy.booking.dto.booking.BookingResponseDto;
import mate.academy.booking.dto.user.UserDataResponseDto;
import mate.academy.booking.model.Accommodation;
import mate.academy.booking.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class TestUtil {

    public static CreateAccommodationRequestDto createAccommodationRequestDto() {
        return new CreateAccommodationRequestDto()
                .setType(Accommodation.Type.BUNGALOW)
                .setAddressID(1L)
                .setSize("Large")
                .setAmenityIds(List.of(1L, 2L))
                .setDailyRate(BigDecimal.valueOf(99))
                .setAvailability(4);
    }

    public static CreateAddressRequestDto createAddressRequestDto() {
        return new CreateAddressRequestDto()
                .setCountry("Ukraine")
                .setCity("Kyiv")
                .setStreet("Mykoly Vasylenka")
                .setNumber("12b")
                .setPostcode("01001");
    }

    public static AddressResponseDto getNewAddressDto() {
        return new AddressResponseDto(
                2L,
                "Ukraine",
                "Kyiv",
                "Mykoly Vasylenka",
                "12b",
                "01001"
        );
    }

    public static CreateAmenityRequestDto createAmenityRequestDto() {
        return new CreateAmenityRequestDto().setName("Free parking");
    }

    public static AmenityResponseDto getNewAmenityDto() {
        return new AmenityResponseDto(3L, "Free parking");
    }

    public static AccommodationResponseDto getNewAccommodationDto() {
        return new AccommodationResponseDto(
                4L, Accommodation.Type.BUNGALOW, getAddressDto(),
                "Large", getAmenityDtos().stream().toList(),
                BigDecimal.valueOf(99), 4
        );
    }

    public static AddressResponseDto getAddressDto() {
        return new AddressResponseDto(1L, "Ukraine",
                "Kyiv", "Khreshchatyk",
                "1a", "01001"
        );
    }

    public static Page<AddressResponseDto> getAddressDtos() {
        List<AddressResponseDto> addresses = List.of(
                new AddressResponseDto(
                        1L,
                        "Ukraine",
                        "Kyiv",
                        "Khreshchatyk",
                        "1a",
                        "01001"
                )
        );
        return new PageImpl<>(
                addresses, PageRequest.of(0, 10),
                addresses.size()
        );
    }

    public static Page<AmenityResponseDto> getAmenityDtos() {
        List<AmenityResponseDto> amenities = List.of(
                new AmenityResponseDto(1L, "WiFi"),
                new AmenityResponseDto(2L, "TV")
        );
        return new PageImpl<>(
                amenities, PageRequest.of(0, 10),
                amenities.size()
        );
    }

    public static Page<AccommodationResponseDto> getAccommodationDtos() {
        List<AccommodationResponseDto> accommodations = List.of(
                new AccommodationResponseDto(
                        1L, Accommodation.Type.APARTMENT, getAddressDto(),
                        "Large", getAmenityDtos().stream().toList(),
                        BigDecimal.valueOf(100), 2
                ),
                new AccommodationResponseDto(
                        2L, Accommodation.Type.HOUSE, getAddressDto(),
                        "Medium", List.of(getAmenityDtos().stream().toList().getFirst()),
                        BigDecimal.valueOf(80), 1
                ),
                new AccommodationResponseDto(
                        3L, Accommodation.Type.CONDO, getAddressDto(),
                        "Small", List.of(getAmenityDtos().stream().toList().getLast()),
                        BigDecimal.valueOf(50), 0
                )
        );
        return new PageImpl<>(
                accommodations, PageRequest.of(0, 10),
                accommodations.size()
        );
    }

    public static Page<AccommodationResponseDtoWithoutAddressDto>
            getAccommodationResponseDtoWithoutAddressDto(
                Long addressId
    ) {
        List<AccommodationResponseDto> fullDtos = getAccommodationDtos().getContent();

        List<AccommodationResponseDtoWithoutAddressDto> dtoWithoutAddress = fullDtos.stream()
                .filter(dto -> dto.location().id().equals(addressId))
                .filter(dto -> dto.availability() > 0)
                .map(dto -> new AccommodationResponseDtoWithoutAddressDto(
                        dto.id(),
                        dto.type(),
                        dto.size(),
                        dto.amenities(),
                        dto.dailyRate(),
                        dto.availability()
                ))
                .toList();

        return new PageImpl<>(
                dtoWithoutAddress,
                PageRequest.of(0, 10),
                dtoWithoutAddress.size()
        );
    }

    public static Page<BookingResponseDto> getBookingsForUser(Long id) {
        List<BookingResponseDto> bookings = List.of(
                new BookingResponseDto(
                        1L,
                        LocalDate.of(2025, 6, 25),
                        LocalDate.of(2025, 6, 26),
                        1L,
                        1L,
                        Booking.Status.CONFIRMED
                ),
                new BookingResponseDto(
                        2L,
                        LocalDate.of(2025, 6, 27),
                        LocalDate.of(2025, 6, 29),
                        2L,
                        1L,
                        Booking.Status.PENDING
                ),
                new BookingResponseDto(
                        3L,
                        LocalDate.of(2025, 6, 18),
                        LocalDate.of(2025, 6, 19),
                        2L,
                        1L,
                        Booking.Status.CONFIRMED
                )
        );

        return new PageImpl<>(bookings, PageRequest.of(0, 10), bookings.size());
    }

    public static UserDataResponseDto getUserProfile() {
        return new UserDataResponseDto(
                1L,
                "user@mail.com",
                "username",
                "user_lastname"
        );
    }
}
