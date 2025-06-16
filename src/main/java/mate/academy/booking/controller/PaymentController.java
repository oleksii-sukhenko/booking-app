package mate.academy.booking.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.dto.payment.PaymentResponseDto;
import mate.academy.booking.model.User;
import mate.academy.booking.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management", description = "Endpoints for payment managing")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestParam Long bookingId,
                                                            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.createPaymentSession(bookingId, user));
    }

    @GetMapping
    public List<PaymentResponseDto> getUserPayments(@RequestParam Long userId) {
        return paymentService.getPaymentSession(userId);
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
