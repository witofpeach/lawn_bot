package ru.home.lawn_bot.cache;

import org.springframework.stereotype.Component;
import ru.home.lawn_bot.botapi.BotState;
import ru.home.lawn_bot.botapi.handlers.fillingprofile.UserApplicationData;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDataCache implements DataCache {
    private Map<Integer, BotState> usersBotStates = new HashMap<>();
    private Map<Integer, UserApplicationData> usersProfileData = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(int userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(int userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.ASK_AGREEMENT;
        }

        return botState;
    }

    @Override
    public UserApplicationData getUserApplicationData(int userId) {
        UserApplicationData userApplicationData = usersProfileData.get(userId);
        if (userApplicationData == null) {
            userApplicationData = new UserApplicationData();
        }
        return userApplicationData;
    }

    @Override
    public void saveUserApplicationData(int userId, UserApplicationData userApplicationData) {
        usersProfileData.put(userId, userApplicationData);
    }
}
