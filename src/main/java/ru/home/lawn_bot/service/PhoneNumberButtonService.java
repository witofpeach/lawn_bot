package ru.home.lawn_bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class PhoneNumberButtonService {

    private LocaleMessageService localeMessageService;

    public PhoneNumberButtonService(LocaleMessageService localeMessageService) {
        this.localeMessageService = localeMessageService;
    }

    public SendMessage getPhoneNumberMessage(final long chatId, final String textMessage) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getPhoneNumberKeyboard();
        final SendMessage phoneNumberMessage =
                createMessageWithKeyboard(chatId, localeMessageService.getMessage(textMessage), replyKeyboardMarkup);

        return phoneNumberMessage;
    }

    private ReplyKeyboardMarkup getPhoneNumberKeyboard() {

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Ввести номер телефона").setRequestContact(true));
        keyboard.add(row1);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private SendMessage createMessageWithKeyboard(final long chatId,
                                                  String textMessage,
                                                  final ReplyKeyboardMarkup replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }
}
