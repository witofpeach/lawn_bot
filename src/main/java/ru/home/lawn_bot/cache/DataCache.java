package ru.home.lawn_bot.cache;

import ru.home.lawn_bot.botapi.BotState;
import ru.home.lawn_bot.botapi.handlers.fillingprofile.UserApplicationData;


public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    UserApplicationData getUserApplicationData(int userId);

    void saveUserApplicationData(int userId, UserApplicationData userApplicationData);
}
