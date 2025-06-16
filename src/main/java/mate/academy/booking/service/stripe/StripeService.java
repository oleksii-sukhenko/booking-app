package mate.academy.booking.service.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import mate.academy.booking.model.Booking;

public interface StripeService {
    Session createStripeSession(Booking booking, BigDecimal amountToPay) throws StripeException;

}
