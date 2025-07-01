package mate.academy.booking.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import mate.academy.booking.service.notification.TelegramNotificationServiceImpl;
import mate.academy.booking.telegram.BookingTelegramBot;
import mate.academy.booking.telegram.TelegramProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TelegramNotificationServiceImplTest {
    @Mock
    private BookingTelegramBot bot;
    @Mock
    private TelegramProperties properties;
    @InjectMocks
    private TelegramNotificationServiceImpl telegramNotificationService;

    @Test
    void notifyAdmin_ShouldSendMessage() {
        String message = "New booking created";
        String chatId = "1234567890";

        when(properties.getChatId()).thenReturn(chatId);

        telegramNotificationService.notifyAdmin(message);

        verify(bot).sendMessage(chatId, message);
    }
}
