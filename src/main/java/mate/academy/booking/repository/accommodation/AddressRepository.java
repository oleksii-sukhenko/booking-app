package mate.academy.booking.repository.accommodation;

import mate.academy.booking.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
