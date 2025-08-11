package mate.academy.booking.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import mate.academy.booking.model.User;
import mate.academy.booking.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:/database/data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail_ExistingEmail_ShouldReturnTrue() {
        boolean exists = userRepository.existsByEmail("user@mail.com");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_NonExistingEmail_ShouldReturnFalse() {
        boolean exists = userRepository.existsByEmail("nonexistent@mail.com");
        assertThat(exists).isFalse();
    }

    @Test
    void findByEmail_ShouldReturnUserWithRoles() {
        Optional<User> optionalUser = userRepository.findByEmail("admin@mail.com");
        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get().getRoles())
                .extracting(role -> role.getRole().name())
                .containsExactly("ROLE_MANAGER");
    }
}

