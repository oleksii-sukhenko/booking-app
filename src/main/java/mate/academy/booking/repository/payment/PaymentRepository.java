package mate.academy.booking.repository.payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import mate.academy.booking.model.Payment;
import mate.academy.booking.model.Payment.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingUserId(Long userId);

    Optional<Payment> findBySessionId(String sessionId);

    List<Payment> findAllByStatusAndCreatedAtBefore(Status status, LocalDateTime time);
}
