package ru.home.lawn_bot.botapi.handlers.fillingprofile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.lawn_bot.botapi.BotState;
import ru.home.lawn_bot.botapi.InputMessageHandler;
import ru.home.lawn_bot.cache.UserDataCache;
import ru.home.lawn_bot.service.NameButtonService;
import ru.home.lawn_bot.service.PhoneNumberButtonService;
import ru.home.lawn_bot.service.ReplyMessagesService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

@Slf4j
@Component
public class FillingApplicationHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;
    private PhoneNumberButtonService phoneNumberButtonService;
    private NameButtonService nameButtonService;
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    String phoneNumberPattern = "^((\\+7|7|8)+([0-9]){10})$";
    String emailPattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    public FillingApplicationHandler(UserDataCache userDataCache,
                                     ReplyMessagesService messagesService, PhoneNumberButtonService phoneNumberButtonService,
                                     NameButtonService nameButtonService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.phoneNumberButtonService = phoneNumberButtonService;
        this.nameButtonService = nameButtonService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_APPLICATION)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_SECOND_NAME);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_APPLICATION;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        UserApplicationData profileData = userDataCache.getUserApplicationData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.ASK_SECOND_NAME)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askSecondName");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NAME);
        }

        if (botState.equals(BotState.ASK_NAME)) {
            profileData.setSecondName(usersAnswer);
            replyToUser = nameButtonService.getNameMessage(chatId, "reply.askName");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_BIRTH_DATE);
        }

        if (botState.equals(BotState.ASK_BIRTH_DATE)) {
            if (inputMsg.hasContact()) {
                profileData.setName(inputMsg.getContact().getFirstName());
            } else if (!inputMsg.getText().isEmpty()) {
                profileData.setName(usersAnswer);
            }

            replyToUser = messagesService.getReplyMessage(chatId, "reply.askBirthDate");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_PHONE_NUMBER);
        }

        if (botState.equals(BotState.ASK_PHONE_NUMBER)) {
            try {
                profileData.setDate(dateFormat.parse(usersAnswer));
            } catch (ParseException e) {
                return messagesService.getReplyMessage(chatId, "reply.askRepeatInput");
            }

            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_EMAIL);
            replyToUser = phoneNumberButtonService.getPhoneNumberMessage(chatId, "reply.askPhoneNumber");
        }

        if (botState.equals(BotState.ASK_EMAIL)) {
            if (!inputMsg.hasContact()) {
                if (Pattern.matches(phoneNumberPattern, usersAnswer)) {
                    profileData.setPhoneNumber(usersAnswer);
                } else {
                    return messagesService.getReplyMessage(chatId, "reply.askRepeatInput");
                }
            } else {
                profileData.setPhoneNumber(inputMsg.getContact().getPhoneNumber());
            }
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askEmail");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_HOW_MUCH);
        }

        if (botState.equals(BotState.ASK_HOW_MUCH)) {
            if (Pattern.matches(emailPattern, usersAnswer)) {
                profileData.setEmail(usersAnswer);
            } else {
                return messagesService.getReplyMessage(chatId, "reply.askRepeatInput");
            }
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askHowMuch");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_FOR_HOW_LONG);
        }

        if (botState.equals(BotState.ASK_FOR_HOW_LONG)) {
            int howMuch;

            try {
                howMuch = Integer.parseInt(usersAnswer);
            } catch (NumberFormatException e) {
                return messagesService.getReplyMessage(chatId, "reply.askRepeatInput");
            }

            if (howMuch > 500 && howMuch <= 5000000) {
                profileData.setHowMuch(howMuch);
            } else {
                return messagesService.getReplyMessage(chatId, "reply.askRepeatInput");
            }

            replyToUser = messagesService.getReplyMessage(chatId, "reply.askForHowLong");
            userDataCache.setUsersCurrentBotState(userId, BotState.APPLICATION_FILLED);
        }

        if (botState.equals(BotState.APPLICATION_FILLED)) {
            int forHowLong;
            try {
                forHowLong = Integer.parseInt(usersAnswer);
            } catch (NumberFormatException e) {
                return messagesService.getReplyMessage(chatId, "reply.askRepeatInput");
            }

            if (forHowLong > 0 && forHowLong <= 60) {
                profileData.setHowMuch(forHowLong);
            } else {
                return messagesService.getReplyMessage(chatId, "reply.askRepeatInput");
            }

            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.applicationFilled");
        }

        userDataCache.saveUserApplicationData(userId, profileData);

        return replyToUser;
    }
}



