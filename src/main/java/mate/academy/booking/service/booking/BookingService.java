package mate.academy.booking.service.booking;

import mate.academy.booking.dto.booking.BookingRequestDto;
import mate.academy.booking.dto.booking.BookingResponseDto;
import mate.academy.booking.dto.booking.BookingSearchParameters;
import mate.academy.booking.dto.booking.UpdateBookingRequestDto;
import mate.academy.booking.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingResponseDto create(BookingRequestDto requestDto, User user);

    Page<BookingResponseDto> findByUserIdAndStatus(
            Pageable pageable, BookingSearchParameters searchParameters
    );

    Page<BookingResponseDto> findForAuthenticatedUser(Pageable pageable);

    BookingResponseDto findById(Long id);

    BookingResponseDto updateBookingDetails(Long id, UpdateBookingRequestDto requestDto);

    void cancelBooking(Long id);
}
