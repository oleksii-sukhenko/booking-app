package mate.academy.booking.repository.accommodation;

import java.util.List;
import mate.academy.booking.model.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    Page<Accommodation> findByLocationIdAndAvailabilityGreaterThan(
            Long addressId,
            int minAvailability,
            Pageable pageable
    );

    @Query("""
            SELECT a FROM  Accommodation a
            JOIN a.amenities am
            WHERE am.id IN :amenityIds
            GROUP BY a
            HAVING COUNT(DISTINCT am.id) = :count
            """
    )
    Page<Accommodation> findByAmenities(
            @Param("amenityIds") List<Long> amenitiesIds,
            @Param("count") long count,
            Pageable pageable);
}
