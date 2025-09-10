package mate.academy.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.academy.booking.dto.booking.BookingRequestDto;
import mate.academy.booking.dto.booking.BookingResponseDto;
import mate.academy.booking.dto.booking.BookingSearchParameters;
import mate.academy.booking.dto.booking.UpdateBookingRequestDto;
import mate.academy.booking.exception.EntityNotFoundException;
import mate.academy.booking.mapper.BookingMapper;
import mate.academy.booking.model.Accommodation;
import mate.academy.booking.model.Booking;
import mate.academy.booking.model.Booking.Status;
import mate.academy.booking.model.User;
import mate.academy.booking.repository.accommodation.AccommodationRepository;
import mate.academy.booking.repository.booking.BookingRepository;
import mate.academy.booking.service.booking.BookingServiceImpl;
import mate.academy.booking.service.notification.TelegramNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private TelegramNotificationService notificationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Accommodation accommodation;
    private User user;
    private BookingRequestDto bookingRequestDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        accommodation = new Accommodation()
                .setId(1L)
                .setAvailability(3)
                .setType(Accommodation.Type.APARTMENT);

        user = new User()
                .setId(1L)
                .setEmail("user@example.com");

        bookingRequestDto = new BookingRequestDto()
                .setAccommodationId(1L)
                .setCheckInDate(LocalDate.now())
                .setCheckOutDate(LocalDate.now().plusDays(2));

        booking = new Booking()
                .setId(1L)
                .setAccommodation(accommodation)
                .setUser(user)
                .setStatus(Booking.Status.PENDING);
    }

    @Test
    void create_ValidBookingRequestAndAvailabilityExists_ReturnsBookingDto() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(bookingRepository.countOverlappingBookings(anyLong(), any(), any())).thenReturn(0L);
        when(bookingMapper.toModel(bookingRequestDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(
                new BookingResponseDto(
                        1L, null, null, null, null, null
                )
        );

        BookingResponseDto result = bookingService.create(bookingRequestDto, user);

        assertNotNull(result);
        verify(notificationService).notifyAdmin(contains("New booking from"));
        verify(bookingRepository).save(booking);
    }

    @Test
    void create_ValidBookingRequestButUnavailable_ReturnsException() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(bookingRepository.countOverlappingBookings(anyLong(), any(), any())).thenReturn(3L);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookingService.create(bookingRequestDto, user));

        assertEquals("There is no available accommodation for this period",
                exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void findByUserIdAndStatus_WithParams_ReturnsPageOfDtos() {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(Status.PENDING);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(Status.CONFIRMED);

        BookingResponseDto dto1 = new BookingResponseDto(
                1L, null, null, null,
                user.getId(), Status.PENDING
        );
        BookingResponseDto dto2 = new BookingResponseDto(
                2L, null, null, null,
                user.getId(), Status.CONFIRMED
        );

        Page<Booking> mockPage = new PageImpl<>(List.of(booking1, booking2));
        final Pageable pageable = PageRequest.of(0, 5);
        final BookingSearchParameters params =
                new BookingSearchParameters(
                        new Long[]{user.getId()},
                        new String[]{"PENDING", "CONFIRMED"}
                );

        when(
                bookingRepository.searchByUserIdsAndStatuses(
                        eq(List.of(user.getId())),
                        eq(List.of(Status.PENDING, Status.CONFIRMED)),
                        eq(pageable)
                )
        ).thenReturn(mockPage);
        when(bookingMapper.toDto(booking1)).thenReturn(dto1);
        when(bookingMapper.toDto(booking2)).thenReturn(dto2);

        Page<BookingResponseDto> result =
                bookingService.findByUserIdAndStatus(pageable, params);

        assertEquals(2, result.getTotalElements());
        assertEquals(Status.PENDING, result.getContent().getFirst().status());
        assertEquals(Status.CONFIRMED, result.getContent().get(1).status());
    }

    @Test
    void findByUserIdAndStatus_WithNullParams_ReturnsAllBookings() {
        BookingSearchParameters params = new BookingSearchParameters(null, null);
        Pageable pageable = PageRequest.of(0, 5);

        BookingResponseDto dto = new BookingResponseDto(
                1L, null, null, null,
                user.getId(), Status.PENDING
        );
        Page<Booking> mockPage = new PageImpl<>(List.of(booking));

        when(bookingRepository.searchByUserIdsAndStatuses(null, null, pageable))
                .thenReturn(mockPage);
        when(bookingMapper.toDto(booking)).thenReturn(dto);

        Page<BookingResponseDto> result = bookingService.findByUserIdAndStatus(pageable, params);

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().getFirst().id());
    }

    @Test
    void findByUserIdAndStatus_WithInvalidStatus_ThrowsException() {
        BookingSearchParameters params = new BookingSearchParameters(
                new Long[]{user.getId()}, new String[]{"INVALID_STATUS"});
        Pageable pageable = PageRequest.of(0, 5);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findByUserIdAndStatus(pageable, params));
    }

    @Test
    void updateBookingDetails_ValidRequest_shouldUpdateAndReturnDto() {
        UpdateBookingRequestDto requestDto = new UpdateBookingRequestDto()
                .setAccommodationId(accommodation.getId())
                .setCheckInDate(LocalDate.now().plusDays(1))
                .setCheckOutDate(LocalDate.now().plusDays(3));

        booking.setAccommodation(accommodation)
                .setCheckInDate(requestDto.getCheckInDate())
                .setCheckOutDate(requestDto.getCheckOutDate())
                .setStatus(Status.PENDING);

        BookingResponseDto expected = new BookingResponseDto(
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getAccommodation().getId(),
                booking.getUser().getId(),
                booking.getStatus()
        );

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(accommodationRepository.findById(accommodation.getId()))
                .thenReturn(Optional.of(accommodation));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(expected);

        BookingResponseDto actual = bookingService
                .updateBookingDetails(booking.getId(), requestDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(bookingRepository).save(booking);
    }

    @Test
    void updateBookingDetails_AccommodationNotFound_ShouldThrowException() {
        Long invalidAccommodationId = 99L;
        UpdateBookingRequestDto requestDto = new UpdateBookingRequestDto()
                .setAccommodationId(invalidAccommodationId);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(accommodationRepository.findById(invalidAccommodationId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.updateBookingDetails(booking.getId(), requestDto));
        assertEquals("Accommodation not found", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancelBooking_ValidId_ShouldSetStatusToCanceledAndSave() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(booking.getId());

        assertEquals(Status.CANCELED, booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancelBooking_BookingNotFound_ShouldThrowException() {
        Long invalidId = 99L;
        when(bookingRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.cancelBooking(invalidId));

        assertEquals("Booking not found", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }
}
