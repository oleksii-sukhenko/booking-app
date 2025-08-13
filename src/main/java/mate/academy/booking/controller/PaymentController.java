package mate.academy.booking.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.payment.PaymentResponseDto;
import mate.academy.booking.model.User;
import mate.academy.booking.service.payment.PaymentService;
import mate.academy.booking.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management", description = "Endpoints for payment managing")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestParam Long bookingId) {
        User user = userService.getCurrentUser();
        PaymentResponseDto payment = paymentService.createPaymentSession(bookingId, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<PaymentResponseDto> getUserPaymentSession(@RequestParam Long userId) {
        return paymentService.getPaymentSession(userId);
    }

    @GetMapping("/my")
    public List<PaymentResponseDto> getMyPaymentSession() {
        User user = userService.getCurrentUser();
        return paymentService.getPaymentSession(user.getId());
    }

    @GetMapping("/success")
    public String handleSuccess(@RequestParam("session_id") String sessionId) {
        return paymentService.handleSuccess(sessionId);
    }

    @GetMapping("/cancel")
    public String handleCancel(@RequestParam("session_id") String sessionId) {
        return paymentService.handleCancel(sessionId);
    }
}
