package ru.home.lawn_bot.botapi.handlers.menu;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.lawn_bot.botapi.BotState;
import ru.home.lawn_bot.botapi.InputMessageHandler;
import ru.home.lawn_bot.service.MainMenuService;
import ru.home.lawn_bot.service.ReplyMessagesService;

@Component
public class MainMenuHandler implements InputMessageHandler {
    private ReplyMessagesService messagesService;
    private MainMenuService mainMenuService;

    public MainMenuHandler(ReplyMessagesService messagesService, MainMenuService mainMenuService) {
        this.messagesService = messagesService;
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(), messagesService.getReplyText("reply.showMainMenu"));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_MAIN_MENU;
    }


}
