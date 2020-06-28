package ru.home.lawn_bot.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.lawn_bot.botapi.BotState;
import ru.home.lawn_bot.botapi.InputMessageHandler;
import ru.home.lawn_bot.botapi.handlers.fillingprofile.UserApplicationData;
import ru.home.lawn_bot.cache.UserDataCache;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Component
public class ShowApplicationHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public ShowApplicationHandler(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        final int userId = message.getFrom().getId();
        final UserApplicationData applicationData = userDataCache.getUserApplicationData(userId);

        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        return new SendMessage(message.getChatId(), String.format("%s%n -------------------%nФамилия: %s%nИмя: %s%nДата рождения: %s%nНомер телефона: %s%nЭлектронная почта: %s%n" +
                        "Мне нужно: %d%nНа срок: %d%n", "Данные по вашей заявке", applicationData.getSecondName(), applicationData.getName(), dateFormat.format(applicationData.getDate()), applicationData.getPhoneNumber(), applicationData.getEmail(),
                applicationData.getHowMuch(), applicationData.getForHowLong()));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_APPLICATION;
    }
}
