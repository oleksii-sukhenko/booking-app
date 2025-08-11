package mate.academy.booking.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import mate.academy.booking.model.Payment;
import mate.academy.booking.repository.payment.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:/database/data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void findByBookingUserId_ValidId_ShouldReturnUserPayments() {
        List<Payment> result = paymentRepository.findByBookingUserId(1L);
        assertThat(result).hasSize(3);
    }

    @Test
    void findBySessionId_ValidSession_ShouldReturnCorrectPayment() {
        Optional<Payment> payment = paymentRepository.findBySessionId("session_abc123");
        assertThat(payment).isPresent();
        assertThat(payment.get().getAmountToPay()).isEqualByComparingTo("80");
    }

    @Test
    void findAllByStatusAndCreatedAtBefore_ShouldReturnExpiredPayments() {
        List<Payment> result = paymentRepository.findAllByStatusAndCreatedAtBefore(
                Payment.Status.PENDING,
                LocalDateTime.now().plusHours(1)
        );
        assertThat(result).hasSize(2);
    }

    @Test
    void findFirstByBookingIdAndStatusOrderByCreatedAtDesc_ShouldReturnLatestPayment() {
        Optional<Payment> result = paymentRepository
                .findFirstByBookingIdAndStatusOrderByCreatedAtDesc(
                1L, Payment.Status.PAID
        );
        assertThat(result).isPresent();
        assertThat(result.get().getSessionId()).isEqualTo("session_def456");
    }
}
