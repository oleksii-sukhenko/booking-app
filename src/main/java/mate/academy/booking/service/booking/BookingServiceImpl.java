package mate.academy.booking.service.booking;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.booking.BookingRequestDto;
import mate.academy.booking.dto.booking.BookingResponseDto;
import mate.academy.booking.dto.booking.BookingSearchParameters;
import mate.academy.booking.dto.booking.UpdateBookingRequestDto;
import mate.academy.booking.mapper.BookingMapper;
import mate.academy.booking.model.Accommodation;
import mate.academy.booking.model.Booking;
import mate.academy.booking.model.Booking.Status;
import mate.academy.booking.model.User;
import mate.academy.booking.repository.accommodation.AccommodationRepository;
import mate.academy.booking.repository.booking.BookingRepository;
import mate.academy.booking.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;

    @Override
    public BookingResponseDto create(BookingRequestDto requestDto, User user) {
        Accommodation accommodation
                = accommodationRepository.findById(requestDto.getAccommodationId())
                .orElseThrow(() -> new EntityNotFoundException("Accommodation not found"));

        Booking booking = bookingMapper.toModel(requestDto);
        booking.setUser(user);
        booking.setAccommodation(accommodation);
        booking.setStatus(Status.PENDING);

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public Page<BookingResponseDto> findByUserIdAndStatus(
            Pageable pageable, BookingSearchParameters searchParameters
    ) {
        List<Long> userIds = searchParameters.userId() != null
                ? Arrays.asList(searchParameters.userId())
                : null;

        List<Status> statuses = searchParameters.status() != null
                ? Arrays.stream(searchParameters.status())
                .map(String::toUpperCase)
                .map(Status::valueOf)
                .toList()
                : null;

        return bookingRepository.searchByUserIdsAndStatuses(userIds, statuses, pageable)
                .map(bookingMapper::toDto);
    }

    @Override
    public Page<BookingResponseDto> findForAuthenticatedUser(Pageable pageable) {
        User user = userService.getCurrentUser();
        return bookingRepository.findAllByUserId(user.getId(), pageable)
                .map(bookingMapper::toDto);
    }

    @Override
    public BookingResponseDto findById(Long id) {
        return bookingMapper.toDto(bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found")));
    }

    @Override
    public BookingResponseDto updateBookingDetails(Long id, UpdateBookingRequestDto requestDto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        Accommodation accommodation = accommodationRepository.findById(
                requestDto.getAccommodationId()
                )
                .orElseThrow(() -> new EntityNotFoundException("Accommodation not found"));

        booking.setCheckInDate(requestDto.getCheckInDate());
        booking.setCheckOutDate(requestDto.getCheckOutDate());
        booking.setAccommodation(accommodation);
        booking.setStatus(Status.PENDING);

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        booking.setStatus(Status.CANCELED);
        bookingRepository.save(booking);
    }

    @Scheduled(cron = "0 00 12 * * *")
    private void setBookingAsExpired() {
        LocalDate today = LocalDate.now();

        List<Status> activeStatuses = List.of(
                Status.PENDING,
                Status.CONFIRMED
        );
        List<Booking> bookingsToExpire = bookingRepository
                .findByCheckOutDateAndStatuses(today, activeStatuses);

        for (Booking booking : bookingsToExpire) {
            booking.setStatus(Status.EXPIRED);
        }

        bookingRepository.saveAll(bookingsToExpire);
    }
}
