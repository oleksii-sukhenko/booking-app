package mate.academy.booking.service.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import mate.academy.booking.model.Booking;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {
    private static final Long CENT_TO_DOLLAR = 100L;
    private static final Long DEFAULT_QUANTITY = 1L;
    private static final String DEFAULT_CURRENCY = "usd";

    @Value("${stripe.success.url}")
    private String successUrl;
    @Value("${stripe.cancel.url}")
    private String cancelUrl;
    @Value("${stripe.secret.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public Session createStripeSession(Booking booking, BigDecimal amountToPay)
            throws StripeException {
        BigDecimal amountInCents = amountToPay.multiply(BigDecimal.valueOf(CENT_TO_DOLLAR));
        Long cents = amountInCents.setScale(0, RoundingMode.HALF_UP).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(DEFAULT_QUANTITY)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(DEFAULT_CURRENCY)
                                                .setUnitAmount(cents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem
                                                                .PriceData.ProductData.builder()
                                                                .setName("Booking " + booking
                                                                        .getAccommodation()
                                                                        .getType()
                                                                        + " #" + booking
                                                                        .getAccommodation()
                                                                        .getId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        return Session.create(params);
    }
}
