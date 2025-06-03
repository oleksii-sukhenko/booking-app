package mate.academy.booking.repository.accommodation;

import mate.academy.booking.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}
