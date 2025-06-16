package mate.academy.booking.telegram;

import mate.academy.booking.service.accommodation.AccommodationService;
import mate.academy.booking.service.booking.BookingService;
import mate.academy.booking.service.user.UserService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BookingTelegramBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final UserSessionStorage sessionStorage;
    private final UserService userService;
    private final AccommodationService accommodationService;
    private final BookingService bookingService;

    public BookingTelegramBot(
            String token,
            String username,
            UserSessionStorage sessionStorage,
            UserService userService,
            AccommodationService accommodationService,
            BookingService bookingService
    ) {
        super(token);
        this.botUsername = username;
        this.sessionStorage = sessionStorage;
        this.userService = userService;
        this.accommodationService = accommodationService;
        this.bookingService = bookingService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText().trim();
        UserSession session = sessionStorage.getSession(chatId);

        switch (session.getMode()) {
            case START -> {
                sendMessage(chatId, "email");
                session.setMode(DialogMode.EMAIL);
            }
            case EMAIL -> {
                sendMessage(chatId, "password");
                session.setMode(DialogMode.PASSWORD);
            }
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Не вдалося надіслати повідомлення", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

}
