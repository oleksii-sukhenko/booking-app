package mate.academy.booking.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import mate.academy.booking.model.Accommodation;
import mate.academy.booking.model.Amenity;
import mate.academy.booking.repository.accommodation.AccommodationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:/database/data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AccommodationRepositoryTest {
    @Autowired
    private AccommodationRepository accommodationRepository;

    @Test
    void findByLocationIdAndAvailabilityGreaterThan_Valid_ShouldReturnPage() {
        Page<Accommodation> page = accommodationRepository
                .findByLocationIdAndAvailabilityGreaterThan(
                        1L, 0, PageRequest.of(0, 10)
                );
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void findByAmenities_ValidAmenities_ShouldReturnPage() {
        Page<Accommodation> page = accommodationRepository.findByAmenities(
                List.of(1L, 2L),
                2,
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().getAmenities().stream()
                .map(Amenity::getId)
                .toList()
        ).containsExactlyInAnyOrder(1L, 2L);
    }
}
