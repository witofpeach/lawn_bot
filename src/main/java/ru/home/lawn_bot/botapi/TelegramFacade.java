package ru.home.lawn_bot.botapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.home.lawn_bot.cache.UserDataCache;
import ru.home.lawn_bot.service.MainMenuService;

@Component
@Slf4j
public class TelegramFacade {
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private MainMenuService mainMenuService;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && (message.hasText() || message.hasContact())) {
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;


        if (inputMsg != null) {
            switch (inputMsg) {
                case "/start":
                    botState = BotState.ASK_AGREEMENT;
                    break;
                case "Подать заявку":
                    botState = BotState.FILLING_APPLICATION;
                    break;
                case "Моя заявка":
                    botState = BotState.SHOW_USER_APPLICATION;
                    break;
                case "Помощь":
                    botState = BotState.SHOW_HELP_MENU;
                    break;
                default:
                    botState = userDataCache.getUsersCurrentBotState(userId);
                    break;
            }
        } else {
            botState = userDataCache.getUsersCurrentBotState(userId);
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }


    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Воспользуйтесь главным меню");


        if (buttonQuery.getData().equals("buttonYes")) {
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        } else if (buttonQuery.getData().equals("buttonNo")) {
            callBackAnswer = sendAnswerCallbackQuery("Возвращайтесь, когда будете готовы", false, buttonQuery);
        }

        return callBackAnswer;


    }


    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }


}
