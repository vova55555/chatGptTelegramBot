package org.hoshta.handler.impl;

import org.hoshta.enums.ConversationState;
import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.stereotype.Component;

import static org.hoshta.enums.ConversationState.*;

@Component
public class NotTextHandler extends UserRequestHandler {
    private final LanguageSelectionHandler languageSelectionHandler;

    public NotTextHandler(UserSessionService userSessionService, TelegramService telegramService,
                          KeyboardHelper keyboardHelper, LanguageSelectionHandler languageSelectionHandler) {
        super(userSessionService, telegramService, keyboardHelper);
        this.languageSelectionHandler = languageSelectionHandler;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return !isTextMessage(request.getUpdate());
    }

    @Override
    public void handle(UserRequest request) {
        Long chatId = request.getChatId();
        ConversationState actualState = getUserSession(request).getState();
        if (actualState.equals(WAITING_FOR_LANGUAGE)) {
            telegramService.sendMessage(chatId, getTranslation(request, "notTextLanguageError"));
            languageSelectionHandler.handle(request);
        } else if (actualState.equals(WAITING_FOR_QUESTION)) {
            telegramService.sendMessage(chatId, getTranslation(request, "notTextChatError"));
            telegramService.sendMessage(chatId, getTranslation(request, "conversationTooltip"));
        } else if(actualState.equals(WAITING_FOR_IMAGE_DESCRIPTION)) {
            telegramService.sendMessage(chatId, getTranslation(request, "notTextChatError"));
        }
        else {
            telegramService.sendMessage(chatId, getTranslation(request, "notTextLanguageError"));
            telegramService.sendMessage(chatId, getTranslation(request, "errorMessagePlanSelection"));
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
