package mate.academy.booking.repository.booking;

import java.time.LocalDate;
import java.util.List;
import mate.academy.booking.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long>,
        JpaSpecificationExecutor<Booking> {
    List<Booking> findAllByUserId(Long userId);

    Page<Booking> findAllByUserId(Long userId, Pageable pageable);

    @Query("""
            SELECT b FROM Booking b
            WHERE (:userIds IS NULL OR b.user.id IN :userIds)
            AND (:statuses IS NULL OR b.status IN :statuses)
            """)
    Page<Booking> searchByUserIdsAndStatuses(
            @Param("userIds") List<Long> userIds,
            @Param("statuses") List<Booking.Status> statuses,
            Pageable pageable
    );

    @Query("""
                SELECT b FROM Booking b
                WHERE b.checkOutDate = :checkOutDate
                AND b.status IN :statuses
            """)
    List<Booking> findByCheckOutDateAndStatuses(
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("statuses") List<Booking.Status> statuses
    );

    @Query("""
                SELECT COUNT(b) FROM Booking b
                WHERE b.accommodation.id = :accommodationId
                AND b.status IN ('PENDING', 'CONFIRMED')
                AND b.checkOutDate > :checkInDate
                AND b.checkInDate < :checkOutDate
            """)
    long countOverlappingBookings(
            @Param("accommodationId") Long accommodationId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}
