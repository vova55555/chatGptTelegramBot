package org.hoshta.handler.impl;

import org.hoshta.enums.ConversationState;
import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Locale;
import java.util.Objects;

@Component
public class StartCommandHandler extends UserRequestHandler {

    public StartCommandHandler(UserSessionService userSessionService, TelegramService telegramService, KeyboardHelper keyboardHelper) {
        super(userSessionService, telegramService, keyboardHelper);
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        String command = "/start";
        return isCommand(request.getUpdate(), command) || getUserSession(request).getState().equals(ConversationState.STARTED);
    }

    @Override
    public void handle(UserRequest request) {
        ReplyKeyboard replyKeyboard = keyboardHelper.buildSelectLanguageMenu();
        String userDefaultLanguage = request.getUpdate().getMessage().getFrom().getLanguageCode();
        Locale userLocale = Objects.isNull(getLocale(request)) ? new Locale(userDefaultLanguage) : getLocale(request);
        setUserSessionStateAndLocale(request, userLocale, ConversationState.WAITING_FOR_LANGUAGE);
        telegramService.sendMessage(request.getChatId(), getTranslation(request, "selectLanguage"), replyKeyboard);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }


    public boolean isCommand(Update update, String command) {
        Message message = update.getMessage();
        return isTextMessage(update) && message.isCommand()
                && Objects.equals(message.getText(), command);
    }
}
