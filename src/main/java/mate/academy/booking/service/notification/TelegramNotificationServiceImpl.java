package mate.academy.booking.service.notification;

import lombok.RequiredArgsConstructor;
import mate.academy.booking.telegram.BookingTelegramBot;
import mate.academy.booking.telegram.TelegramProperties;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationServiceImpl implements TelegramNotificationService {
    private final BookingTelegramBot bot;
    private final TelegramProperties properties;

    @Override
    public void notifyAdmin(String message) {
        bot.sendMessage(properties.getChatId(), message);
    }
}
