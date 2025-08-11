package mate.academy.booking.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import mate.academy.booking.model.Booking;
import mate.academy.booking.repository.booking.BookingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:/database/data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findAllByUserId_ValidUserId_shouldReturnPage() {
        Page<Booking> bookings = bookingRepository.findAllByUserId(
                1L, PageRequest.of(0, 10)
        );
        assertThat(bookings.getTotalElements()).isEqualTo(3);
        assertThat(bookings.getContent()).hasSize(3);
    }

    @Test
    void searchByUserIdAndStatuses_ValidData_ShouldReturnMatchingBookings() {
        Page<Booking> result = bookingRepository.searchByUserIdsAndStatuses(
                List.of(1L),
                List.of(Booking.Status.CONFIRMED),
                PageRequest.of(0, 10)
        );
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void findByCheckoutDatesAndStatuses_OneForDate_ShouldReturnCorrectBookings() {
        List<Booking> result = bookingRepository.findByCheckOutDateAndStatuses(
                LocalDate.of(2025, 6,29),
                List.of(Booking.Status.CONFIRMED, Booking.Status.PENDING)
        );
        assertThat(result).hasSize(1);
    }

    @Test
    void countOverlappingBookings_TwoInDates_ShouldReturnExpectedCount() {
        long count = bookingRepository.countOverlappingBookings(
                2L,
                LocalDate.of(2025, 6, 18),
                LocalDate.of(2025, 6, 29)
        );
        assertThat(count).isEqualTo(2);
    }
}
