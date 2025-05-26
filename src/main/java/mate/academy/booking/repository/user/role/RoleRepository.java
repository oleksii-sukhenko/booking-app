package mate.academy.booking.repository.user.role;

import mate.academy.booking.model.Role;
import mate.academy.booking.model.Role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(RoleName roleName);
}
