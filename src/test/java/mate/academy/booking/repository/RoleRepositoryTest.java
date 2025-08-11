package mate.academy.booking.repository;

import static org.assertj.core.api.Assertions.assertThat;

import mate.academy.booking.model.Role;
import mate.academy.booking.repository.user.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:/database/data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByRole_ExistingRole_ShouldReturnRole() {
        Role role = roleRepository.findByRole(Role.RoleName.ROLE_CUSTOMER);
        assertThat(role).isNotNull();
        assertThat(role.getRole()).isEqualTo(Role.RoleName.ROLE_CUSTOMER);
    }
}
