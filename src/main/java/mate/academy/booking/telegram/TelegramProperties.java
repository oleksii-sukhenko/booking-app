package mate.academy.booking.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram.bot")
@Getter
@Setter
public class TelegramProperties {
    private String token;
    private String username;
    private String chatId;
}
